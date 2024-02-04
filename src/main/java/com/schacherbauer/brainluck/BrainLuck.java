package com.schacherbauer.brainluck;

import com.schacherbauer.brainluck.internal.ProgramState;

public class BrainLuck {
    private final String code;

    public BrainLuck(String code) {
        this.code = code;
    }

    public String process(String input) {
        var programState = new ProgramState(code, input);
        while (!programState.isFinished()) {
            programState.executeStep();
        }
        return programState.getResult();
    }
}
