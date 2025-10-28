package com.cluewave.dto;

/**
 * Data transfer object representing a Player.  Contains only the fields that
 * should be exposed to the client: id, name and score.  Internal details
 * (such as connection state) remain hidden.
 */
public class PlayerDTO {
    private final String id;
    private final String name;
    private final int score;

    public PlayerDTO(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}