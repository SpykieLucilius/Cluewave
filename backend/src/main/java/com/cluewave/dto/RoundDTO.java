package com.cluewave.dto;

/**
 * Data transfer object representing a Round.  Contains only the public
 * information for a round exposed to clients: left and right prompts and
 * whether the result has been revealed.  The internal target position is
 * not sent to players until the round is completed.
 */
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