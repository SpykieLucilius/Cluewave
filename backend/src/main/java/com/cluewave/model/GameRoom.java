package com.cluewave.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a game room. The code uniquely identifies the room and is used
 * by players to join. Each room keeps track of connected players, the
 * current round (if any) and a simple string representing the state of the
 * room (e.g. "lobby", "in_round", "results"). All state is kept in memory.
 */
public class GameRoom {
    private final String code;
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private Round currentRound;
    private String state;

    public GameRoom(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}