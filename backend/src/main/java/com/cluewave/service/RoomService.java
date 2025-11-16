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
 * Service responsible for creating and managing game rooms.  All room
 * information lives in an in‑memory map keyed by the room code.  In a
 * production environment you would likely replace this with a shared cache
 * or database.  This service also enforces a two‑player limit and provides
 * methods to join rooms either by code or by the host’s email.
 */
@Service
public class RoomService {
    /**
     * Rooms are stored in a concurrent map keyed by the room code.  Each
     * GameRoom instance contains its own players map.
     */
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    /**
     * Creates a new room for the given host.  A random code is generated
     * and the host is automatically added to the room.  The host’s name and
     * email are stored on the GameRoom for later lookup.
     *
     * @param hostName  the username of the player creating the room
     * @param hostEmail the email address of the player creating the room
     * @return a DTO representing the new room state
     */
    public RoomDTO createRoom(String hostName, String hostEmail) {
        String code = generateCode();
        GameRoom room = new GameRoom(code, hostName, hostEmail);
        room.setState("lobby");

        // create and register host player
        Player host = new Player(UUID.randomUUID().toString(), hostName);
        room.getPlayers().put(host.getId(), host);

        rooms.put(code, room);
        return toDTO(room);
    }

    /**
     * Adds a new player to an existing room by code.  If the room already
     * contains two players (host + guest), an exception is thrown.
     *
     * @param code       the room code
     * @param playerName the display name of the joining player
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
     * Attempts to join a room using the host’s email.  If a matching room is
     * found, the provided player is added as the second player.  If no
     * matching room exists or the room is full, an exception is thrown.
     *
     * @param hostEmail  the email of the host who created the room
     * @param playerName the display name of the joining player
     * @return a DTO representing the updated room
     */
    public RoomDTO joinRoomByEmail(String hostEmail, String playerName) {
        GameRoom room = rooms.values().stream()
                .filter(r -> r.getHostEmail().equalsIgnoreCase(hostEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Room not found for host email: " + hostEmail));
        joinRoom(room.getCode(), playerName);
        return toDTO(room);
    }

    /**
     * Retrieves the current state of the room as a DTO.
     *
     * @param code the room code
     * @return a DTO with room details
     */
    public RoomDTO getRoomState(String code) {
        return toDTO(getOrThrow(code));
    }

    /**
     * Starts a new round for the specified room.  For now the prompts and
     * target values are placeholder values.  The room state transitions
     * from "lobby" to "in_round".
     *
     * @param code the room code
     * @return a DTO representing the newly created round
     */
    public RoundDTO startRound(String code) {
        GameRoom room = getOrThrow(code);
        Round round = new Round();
        // Placeholder prompts – in a production version these would be
        // generated or pulled from a database.
        round.setPromptLeft("Froid");
        round.setPromptRight("Chaud");
        round.setTargetPosition(new Random().nextDouble());
        round.setRevealed(false);
        room.setCurrentRound(round);
        room.setState("in_round");
        return toRoundDTO(round);
    }

    /**
     * Helper to get a room by code or throw an exception if not found.
     */
    private GameRoom getOrThrow(String code) {
        GameRoom room = rooms.get(code);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + code);
        }
        return room;
    }

    /**
     * Generates a unique 4‑character room code consisting of uppercase
     * letters and digits.  The code excludes ambiguous characters (I, O, 0, 1).
     */
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

    /**
     * Converts a GameRoom into a RoomDTO for transmission to clients.
     */
    private RoomDTO toDTO(GameRoom room) {
        List<PlayerDTO> players = room.getPlayers().values().stream()
                .map(p -> new PlayerDTO(p.getId(), p.getName(), p.getScore()))
                .collect(Collectors.toList());
        RoundDTO roundDTO = null;
        if (room.getCurrentRound() != null) {
            roundDTO = toRoundDTO(room.getCurrentRound());
        }
        return new RoomDTO(room.getCode(), players, roundDTO, room.getState());
    }

    /**
     * Converts a Round into a DTO with only the necessary public fields.
     */
    private RoundDTO toRoundDTO(Round round) {
        return new RoundDTO(round.getPromptLeft(), round.getPromptRight(), round.isRevealed());
    }
}