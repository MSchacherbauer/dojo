package com.schacherbauer;

import lombok.Data;

import java.util.Arrays;
import java.util.stream.IntStream;

public class BrainLuck {


    private final String code;

    public BrainLuck(String code) {
        this.code = code;
    }

    public String process(String input) {
        var programState = new ProgramState(code, input);
        while (!programState.finished) {
            getOperator(programState).execute(programState);
            if (programState.instructionPointer >= programState.code.length()) {
                programState.finished = true;
            }
        }
        return programState.output.toString();
    }

    private static Operator getOperator(ProgramState programState) {
        return Arrays.stream(Operator.values()).filter(op -> op.matches(programState)).findFirst().orElseThrow();
    }

    static class Memory {

        private final int[] memory;
        private int dataPointer;

        public Memory(int size) {
            this.memory = IntStream.range(0, size).map(i -> 0).toArray();
            this.dataPointer = size / 2;
        }

        public void increment() {
            var i = memory[dataPointer];
            memory[dataPointer] = i == 255 ? 0 : i + 1;
        }

        public void store(char value) {
            memory[dataPointer] = value;
        }

        public void incrementDp() {
            dataPointer++;
        }

        public void decrementDp() {
            dataPointer--;
        }

        public void decrement() {
            var i = memory[dataPointer];
            memory[dataPointer] = i == 0 ? 255 : i - 1;
        }

        public char getValue() {
            return ((char) memory[dataPointer]);
        }

        @Override
        public String toString() {
            var max = Math.max(0, dataPointer - 5);
            var min = Math.min(memory.length, dataPointer + 5);
            StringBuilder sb = new StringBuilder("[");
            for (int i = max; i < min; i++) {
                if (i == dataPointer) {
                    sb.append("(").append(memory[i]).append(")");
                } else {
                    sb.append(memory[i]);
                }
            }
            return sb.append("]").toString();
        }
    }

    @Data
    static class ProgramState {
        private int instructionPointer;
        private int inputStreamPointer;

        private Memory memory;
        private String code;
        private String input;
        private StringBuilder output;
        private boolean finished;

        public ProgramState(String code, String input) {
            this.code = code;
            this.input = input;
            this.instructionPointer = 0;
            this.memory = new Memory(2024);
            this.finished = false;
            this.inputStreamPointer = 0;
            this.output = new StringBuilder();
        }

        public int findMatchingBracketIndex() {
            int openBrackets = 0;
            return switch (code.charAt(instructionPointer)) {
                case '[' -> {
                    for (int i = inputStreamPointer; i < code.length(); i++) {
                        if (code.charAt(i) == '[') {
                            openBrackets++;
                        }
                        if (code.charAt(i) == ']') {
                            openBrackets--;
                        }
                        if (openBrackets == 0) {
                            yield i;
                        }
                    }
                    finished = true;
                    yield -1;
                }
                case ']' -> {
                    for (int i = inputStreamPointer; i >= 0; i--) {
                        if (code.charAt(i) == '[') {
                            openBrackets--;
                        }
                        if (code.charAt(i) == ']') {
                            openBrackets++;
                        }
                        if (openBrackets == 0) {
                            yield i;
                        }
                    }
                    finished = true;
                    yield -1;
                }
                default -> {
                    finished = true;
                    yield -1;
                }
            };
        }
    }


    enum Operator {
        INCREMENT_P('>') {
            @Override
            void execute(ProgramState state) {
                state.memory.incrementDp();
                state.instructionPointer++;
            }
        },
        DECREMENT_P('<') {
            @Override
            void execute(ProgramState state) {
                state.memory.decrementDp();
                state.instructionPointer++;
            }
        },
        INCREMENT('+') {
            @Override
            void execute(ProgramState state) {
                state.memory.increment();
                state.instructionPointer++;
            }
        },
        DECREMENT('-') {
            @Override
            void execute(ProgramState state) {
                state.memory.decrement();
                state.instructionPointer++;
            }
        },
        OUTPUT_P('.') {
            @Override
            void execute(ProgramState state) {
                var b = state.memory.getValue();
                state.output.append(b);
                state.instructionPointer++;
            }
        },
        STORE_P(',') {
            @Override
            void execute(ProgramState state) {
                state.memory.store(state.input.charAt(state.inputStreamPointer));
                state.inputStreamPointer++;
                state.instructionPointer++;
            }
        },
        JUMP_FORWARD('[') {
            @Override
            void execute(ProgramState state) {
                if (state.memory.getValue() == 0) {
                    state.instructionPointer = state.findMatchingBracketIndex() + 1;
                } else {
                    state.instructionPointer++;
                }
            }
        },
        JUMP_BACKWARD(']') {
            @Override
            void execute(ProgramState state) {
                if (state.memory.getValue() != 0) {
                    state.instructionPointer = state.findMatchingBracketIndex() + 1;
                } else {
                    state.instructionPointer++;
                }
            }
        };

        private final char c;

        Operator(char c) {
            this.c = c;
        }

        boolean matches(ProgramState state) {
            return state.code.charAt(state.instructionPointer) == c;
        }

        abstract void execute(ProgramState state);

    }
}
