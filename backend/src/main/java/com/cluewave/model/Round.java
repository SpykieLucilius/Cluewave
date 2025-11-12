package com.cluewave.model;

public class Round {
    private String promptLeft;
    private String promptRight;
    private double targetPosition;
    private boolean revealed;

    public String getPromptLeft() {
        return promptLeft;
    }

    public void setPromptLeft(String promptLeft) {
        this.promptLeft = promptLeft;
    }

    public String getPromptRight() {
        return promptRight;
    }

    public void setPromptRight(String promptRight) {
        this.promptRight = promptRight;
    }

    public double getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}