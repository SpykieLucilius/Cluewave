// ---------------------------------------------------------------------
// GAME ROOM SERVICE
// Manages creation, joining, and state transitions of in-memory game rooms.
// Generates unique room codes, enforces two-player limit, supports join by email,
// starts rounds with placeholder prompts, and converts models to DTOs for clients.
// ---------------------------------------------------------------------

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

@Service
public class RoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    public RoomDTO createRoom(String hostName, String hostEmail) {
        String code = generateCode();
        GameRoom room = new GameRoom(code, hostName, hostEmail);
        room.setState("lobby");

        Player host = new Player(UUID.randomUUID().toString(), hostName);
        room.getPlayers().put(host.getId(), host);
        rooms.put(code, room);
        return toDTO(room);
    }

    public PlayerDTO joinRoom(String code, String playerName) {
        GameRoom room = getOrThrow(code);
        if (room.getPlayers().size() >= 2) {
            throw new IllegalStateException("Room is full");
        }
        Player p = new Player(UUID.randomUUID().toString(), playerName);
        room.getPlayers().put(p.getId(), p);
        return new PlayerDTO(p.getId(), p.getName(), p.getScore());
    }

    public RoomDTO joinRoomByEmail(String email, String playerName) {
        GameRoom room = rooms.values().stream()
                .filter(r -> r.getHostEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room not found for host email: " + email));
        joinRoom(room.getCode(), playerName);
        return toDTO(room);
    }

    public RoomDTO getRoomState(String code) {
        return toDTO(getOrThrow(code));
    }

    public RoundDTO startRound(String code) {
        GameRoom room = getOrThrow(code);
        Round round = new Round();
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
        return new RoomDTO(room.getCode(), players, roundDTO, room.getState(),
                room.getHostName(), room.getHostEmail());
    }

    private RoundDTO toRoundDTO(Round r) {
        return new RoundDTO(r.getPromptLeft(), r.getPromptRight(), r.isRevealed());
    }
}