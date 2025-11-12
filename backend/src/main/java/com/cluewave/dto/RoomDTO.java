package com.cluewave.dto;

import java.util.List;

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