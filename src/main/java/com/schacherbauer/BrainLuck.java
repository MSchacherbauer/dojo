package com.schacherbauer;

import lombok.Data;

import java.util.Arrays;

public class BrainLuck {


    private final String code;

    public BrainLuck(String code) {
        this.code = code;
    }

    public String process(String input) {
        var programState = new ProgramState(code, input);
        while (!programState.finished) {
            var stateBefore = programState;
            programState = Arrays.stream(Operator.values())
                    .filter(operator -> operator.matches(stateBefore))
                    .findFirst()
                    .map(operator -> operator.execute(stateBefore))
                    .orElseGet(() -> {
                        stateBefore.finished = true;
                        return stateBefore;
                    });
        }
        return programState.memory[programState.dataPointer].toString();
    }


    @Data
    static class ProgramState {
        private int instructionPointer;
        private int dataPointer;
        private Byte[] memory;
        private String code;
        private String input;
        private boolean finished;

        public ProgramState(String code, String input) {
            this.code = code;
            this.input = input;
            this.instructionPointer = 0;
            this.dataPointer = 1024;
            this.memory = new Byte[2048];
            this.finished = false;
        }
    }


    enum Operator {
        INCREMENT_P('>') ,
        DECREMENT_P('<'),
        INCREMENT('+'),
        DECREMENT('+'),
        OUTPUT_P('+'),
        STORE_P('+'),
        JUMP_FORWARD('['),
        JUMP_BACKWARD(']');

        private final char c;

        Operator(char c) {
            this.c = c;
        }

        boolean matches(ProgramState state) {
            return state.code.charAt(state.instructionPointer) == c;
        }

        ProgramState execute(ProgramState inputState) {
            return inputState;
        }

    }
}
