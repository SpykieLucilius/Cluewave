package com.cluewave.dto;

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