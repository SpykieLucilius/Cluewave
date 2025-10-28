package com.cluewave.model;

/**
 * Represents a round in a game. A round contains two prompts (left and
 * right), a target position representing the correct answer on a 0..1 scale
 * (e.g. where the true answer lies between the two extremes) and a flag
 * indicating whether the result has been revealed to players.
 */
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