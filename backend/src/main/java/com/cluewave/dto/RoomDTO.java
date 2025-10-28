package com.cluewave.dto;

import java.util.List;

/**
 * Data transfer object representing a GameRoom.  Exposes only the data
 * required by the frontend: room code, list of players, the current round
 * (if any) and the room state.  Using a DTO decouples the internal model
 * from the JSON sent over the wire.
 */
public class RoomDTO {
    private final String code;
    private final List<PlayerDTO> players;
    private final RoundDTO currentRound;
    private final String state;

    public RoomDTO(String code, List<PlayerDTO> players, RoundDTO currentRound, String state) {
        this.code = code;
        this.players = players;
        this.currentRound = currentRound;
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public RoundDTO getCurrentRound() {
        return currentRound;
    }

    public String getState() {
        return state;
    }
}