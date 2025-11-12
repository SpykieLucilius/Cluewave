package com.cluewave.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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