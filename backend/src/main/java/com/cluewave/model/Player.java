// ---------------------------------------------------------------------
// IN-MEMORY PLAYER MODEL
// Represents a player within a room with unique id, name, and score.
// Provides a method to increment score when points are earned.
// ---------------------------------------------------------------------

package com.cluewave.model;

public class Player {
    private final String id;
    private final String name;
    private int score;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
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

    public void addScore(int delta) {
        this.score += delta;
    }
}