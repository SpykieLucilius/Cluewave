// ---------------------------------------------------------------------
// IN-MEMORY GAMEROOM MODEL
// Holds the state of a game room including code, host details, players, current round, and state.
// Uses a concurrent map for players since rooms are stored globally across threads.
// ---------------------------------------------------------------------

package com.cluewave.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom {
    private final String code;
    private final String hostName;
    private final String hostEmail;
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private Round currentRound;
    private String state;

    public GameRoom(String code, String hostName, String hostEmail) {
        this.code = code;
        this.hostName = hostName;
        this.hostEmail = hostEmail;
    }

    public String getCode() {
        return code;
    }

    public String getHostName() {
        return hostName;
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