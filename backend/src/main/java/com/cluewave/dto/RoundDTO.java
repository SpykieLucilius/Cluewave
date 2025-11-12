package com.cluewave.dto;

public class RoundDTO {
    private final String promptLeft;
    private final String promptRight;
    private final boolean revealed;

    public RoundDTO(String promptLeft, String promptRight, boolean revealed) {
        this.promptLeft = promptLeft;
        this.promptRight = promptRight;
        this.revealed = revealed;
    }

    public String getPromptLeft() {
        return promptLeft;
    }

    public String getPromptRight() {
        return promptRight;
    }

    public boolean isRevealed() {
        return revealed;
    }
}