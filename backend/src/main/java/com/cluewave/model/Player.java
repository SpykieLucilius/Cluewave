package com.cluewave.model;

/**
 * Represents a player in a game room. Each player has a unique ID, a name
 * provided when joining the room, and an integer score that accumulates over
 * rounds. Score management is handled by the RoomService.
 */
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

    /**
     * Adds the provided delta to the player's score.  Negative values are
     * permitted to decrement the score.
     *
     * @param delta amount to add to the score
     */
    public void addScore(int delta) {
        this.score += delta;
    }
}