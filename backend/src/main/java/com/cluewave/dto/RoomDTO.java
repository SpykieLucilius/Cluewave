// ---------------------------------------------------------------------
// ROOM DTO
// Represents the state of a game room including players, current round, and host info.
// Encapsulates room code, list of PlayerDTOs, current round, state, and host details.
// ---------------------------------------------------------------------

package com.cluewave.dto;

import java.util.List;

public class RoomDTO {
    private final String code;
    private final List<PlayerDTO> players;
    private final RoundDTO currentRound;
    private final String state;
    private final String hostName;
    private final String hostEmail;

    public RoomDTO(String code, List<PlayerDTO> players, RoundDTO currentRound,
                   String state, String hostName, String hostEmail) {
        this.code = code;
        this.players = players;
        this.currentRound = currentRound;
        this.state = state;
        this.hostName = hostName;
        this.hostEmail = hostEmail;
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

    public String getHostName() {
        return hostName;
    }

    public String getHostEmail() {
        return hostEmail;
    }
}