package com.cluewave.dto;

import java.util.List;

public class RoomDTO {
    private final String code;
    private final List<PlayerDTO> players;
    private final RoundDTO currentRound;
    private final String state;
    /**
     * Name of the host who created the room.  Included so clients can
     * display who is hosting and to determine who can start the game.
     */
    private final String hostName;
    /**
     * Email of the host who created the room.  Included for informational
     * purposes and may be used on the client side to identify the room
     * creator.
     */
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