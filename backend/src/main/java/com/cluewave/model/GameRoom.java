package com.cluewave.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the state of an in‑memory game room.  A room holds exactly two
 * players – a host and a single guest – and tracks minimal state required
 * for play.  The host’s username and email are stored to allow lookup by
 * email when a friend wishes to join without a code.  The room state can
 * be {@code "lobby"} (waiting for players) or {@code "in_round"} when a
 * round is active.
 */
public class GameRoom {
    /**
     * Unique room code used to identify the room.  Codes are short and
     * intentionally exclude ambiguous characters.
     */
    private final String code;

    /**
     * The username of the player who created the room.  Used for display
     * purposes and for the initial player entry in the players map.
     */
    private final String hostName;

    /**
     * The email of the player who created the room.  This value allows
     * another player to locate the room by searching for the host’s email.
     */
    private final String hostEmail;

    /**
     * Map of players currently in the room, keyed by an internal UUID.  A
     * ConcurrentHashMap is used because rooms are stored in a static map
     * shared across threads.
     */
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    /** The current round being played, if any. */
    private Round currentRound;

    /** String representing the overall state of the room (e.g. lobby, in_round). */
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