package com.cluewave.service;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import com.cluewave.model.GameRoom;
import com.cluewave.model.Player;
import com.cluewave.model.Round;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service responsible for creating, joining and managing game rooms.  Rooms are
 * stored in memory using a concurrent map keyed by the room code.  Only two
 * players are allowed per room; attempts to join a full room result in an
 * exception.  Rooms may also be joined via the host's email address.
 */
@Service
public class RoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    /**
     * Creates a new game room with the given host name and host email.
     * A unique code is generated and a host player is registered in the
     * room's player map.  The host's name and email are stored on the
     * room for later lookup by email and to display who created the room.
     *
     * @param hostName  the display name of the host
     * @param hostEmail the email address of the host
     * @return a DTO representing the newly created room
     */
    public RoomDTO createRoom(String hostName, String hostEmail) {
        String code = generateCode();
        // include both hostName and hostEmail when constructing the room
        GameRoom room = new GameRoom(code, hostName, hostEmail);
        room.setState("lobby");

        // create and add host
        Player host = new Player(UUID.randomUUID().toString(), hostName);
        room.getPlayers().put(host.getId(), host);
        rooms.put(code, room);
        return toDTO(room);
    }

    /**
     * Joins an existing room by its code.  If the room already has two
     * participants an IllegalStateException is thrown.
     *
     * @param code       the code of the room
     * @param playerName the name of the joining player
     * @return a DTO representing the joining player
     */
    public PlayerDTO joinRoom(String code, String playerName) {
        GameRoom room = getOrThrow(code);
        if (room.getPlayers().size() >= 2) {
            throw new IllegalStateException("Room is full");
        }
        Player p = new Player(UUID.randomUUID().toString(), playerName);
        room.getPlayers().put(p.getId(), p);
        return new PlayerDTO(p.getId(), p.getName(), p.getScore());
    }

    /**
     * Joins a room by the host's email address.  If no room exists for the
     * specified email or the room is full, an exception is thrown.  After
     * joining the player the updated room DTO is returned.
     *
     * @param email      the email address of the host
     * @param playerName the name of the player attempting to join
     * @return the updated RoomDTO
     */
    public RoomDTO joinRoomByEmail(String email, String playerName) {
        GameRoom room = rooms.values().stream()
                .filter(r -> r.getHostEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room not found for host email: " + email));
        joinRoom(room.getCode(), playerName);
        return toDTO(room);
    }

    /**
     * Retrieves the current state of the room.
     *
     * @param code the room code
     * @return the room as a DTO
     */
    public RoomDTO getRoomState(String code) {
        return toDTO(getOrThrow(code));
    }

    /**
     * Starts a new round for the given room.  Placeholder prompts and a random
     * target position are assigned and the room's state is updated.
     *
     * @param code the room code
     * @return a DTO representing the new round
     */
    public RoundDTO startRound(String code) {
        GameRoom room = getOrThrow(code);
        Round round = new Round();
        // placeholder prompts – these can be replaced with real data
        round.setPromptLeft("Froid");
        round.setPromptRight("Chaud");
        round.setTargetPosition(new Random().nextDouble());
        round.setRevealed(false);
        room.setCurrentRound(round);
        room.setState("in_round");
        return toRoundDTO(round);
    }

    private GameRoom getOrThrow(String code) {
        GameRoom room = rooms.get(code);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + code);
        }
        return room;
    }

    private String generateCode() {
        String letters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(letters.charAt(r.nextInt(letters.length())));
            }
            code = sb.toString();
        } while (rooms.containsKey(code));
        return code;
    }

    private RoomDTO toDTO(GameRoom room) {
        List<PlayerDTO> players = room.getPlayers().values().stream()
                .map(p -> new PlayerDTO(p.getId(), p.getName(), p.getScore()))
                .collect(Collectors.toList());
        RoundDTO roundDTO = null;
        if (room.getCurrentRound() != null) {
            roundDTO = toRoundDTO(room.getCurrentRound());
        }
        // include hostName and hostEmail in the DTO so clients know who created the room
        return new RoomDTO(room.getCode(), players, roundDTO, room.getState(),
                room.getHostName(), room.getHostEmail());
    }

    private RoundDTO toRoundDTO(Round r) {
        return new RoundDTO(r.getPromptLeft(), r.getPromptRight(), r.isRevealed());
    }
}