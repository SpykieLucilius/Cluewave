package com.cluewave.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a game room.  Each room has a unique code, a host email and
 * keeps track of its players and current round.  Only two players may join
 * a room.  State and round information are managed by the service layer.
 */
public class GameRoom {
    private final String code;
    private final String hostEmail;
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private Round currentRound;
    private String state;

    public GameRoom(String code, String hostEmail) {
        this.code = code;
        this.hostEmail = hostEmail;
    }

    public String getCode() {
        return code;
    }

    public String getHostEmail() {
        return hostEmail;
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