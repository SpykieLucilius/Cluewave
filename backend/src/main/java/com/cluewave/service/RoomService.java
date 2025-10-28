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
 * Service responsible for managing game rooms and their state.  All state is
 * stored in memory in a concurrent map keyed by the room code.  Methods are
 * provided to create rooms, join rooms, start rounds and retrieve room
 * state.  In a production environment you may wish to move this state
 * into a persistent store or external cache to support multiple instances.
 */
@Service
public class RoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    /**
     * Creates a new room and registers the host player as the first member.
     *
     * @param hostName the name of the host creating the room
     * @return a DTO representing the room state
     */
    public RoomDTO createRoom(String hostName) {
        String code = generateCode();
        GameRoom room = new GameRoom(code);
        room.setState("lobby");
        rooms.put(code, room);

        // create host player
        Player host = new Player(UUID.randomUUID().toString(), hostName);
        room.getPlayers().put(host.getId(), host);

        return toDTO(room);
    }

    /**
     * Adds a new player to an existing room.
     *
     * @param code the room code
     * @param playerName the name of the joining player
     * @return the created PlayerDTO
     */
    public PlayerDTO joinRoom(String code, String playerName) {
        GameRoom room = getOrThrow(code);
        Player p = new Player(UUID.randomUUID().toString(), playerName);
        room.getPlayers().put(p.getId(), p);
        return new PlayerDTO(p.getId(), p.getName(), p.getScore());
    }

    /**
     * Returns the current state of the room.
     *
     * @param code the room code
     * @return a DTO with room details
     */
    public RoomDTO getRoomState(String code) {
        return toDTO(getOrThrow(code));
    }

    /**
     * Starts a new round for the given room.  Generates placeholder prompts
     * and a random target position.  Updates the room state to `in_round`.
     *
     * @param code the room code
     * @return a DTO representing the new round
     */
    public RoundDTO startRound(String code) {
        GameRoom room = getOrThrow(code);
        Round round = new Round();

        // Placeholder prompts – in a real implementation these would come from a database
        // or generated content.  For now we use simple fixed strings.
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
        StringBuilder sb;
        String code;
        do {
            sb = new StringBuilder();
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
        return new RoomDTO(room.getCode(), players, roundDTO, room.getState());
    }

    private RoundDTO toRoundDTO(Round r) {
        return new RoundDTO(r.getPromptLeft(), r.getPromptRight(), r.isRevealed());
    }
}