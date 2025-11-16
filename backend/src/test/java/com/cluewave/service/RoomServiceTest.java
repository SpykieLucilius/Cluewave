package com.cluewave.service;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RoomService} class.  These tests exercise the core
 * business logic around creating rooms, joining rooms by code or email,
 * limiting the number of players and starting rounds.  They do not rely on
 * Spring infrastructure and can be executed as plain JUnit 5 tests.
 */
public class RoomServiceTest {

    /**
     * Creating a room should return a {@link RoomDTO} with a four character code,
     * contain the host as the only player and initialise the state to "lobby".
     */
    @Test
    void createRoomCreatesHost() {
        RoomService roomService = new RoomService();
        RoomDTO dto = roomService.createRoom("Alice", "alice@example.com");

        assertNotNull(dto.getCode(), "Room code should not be null");
        assertEquals(4, dto.getCode().length(), "Room code should be 4 characters");
        assertEquals("lobby", dto.getState(), "New rooms should start in the lobby state");
        List<?> players = dto.getPlayers();
        assertEquals(1, players.size(), "Room should have one player initially");
        PlayerDTO host = (PlayerDTO) players.get(0);
        assertEquals("Alice", host.getName(), "Host name should match the provided value");
        assertEquals(0, host.getScore(), "New players should start with 0 score");
    }

    /**
     * Joining a room by its code should add the player to the room.  A room can
     * only contain two players; attempting to join a full room should result
     * in an {@link IllegalStateException} with the message "Room is full".
     */
    @Test
    void joinRoomAddsPlayerAndEnforcesLimit() {
        RoomService roomService = new RoomService();
        RoomDTO dto = roomService.createRoom("Host", "host@example.com");
        String code = dto.getCode();

        // join first guest
        PlayerDTO p1 = roomService.joinRoom(code, "Bob");
        assertNotNull(p1.getId(), "Joining player should have a generated ID");
        assertEquals("Bob", p1.getName(), "Joining player's name should match");

        // After second player, room should have 2 players
        RoomDTO state = roomService.getRoomState(code);
        assertEquals(2, state.getPlayers().size(), "Room should contain two players after a guest joins");

        // Attempt to add third player should throw exception
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> roomService.joinRoom(code, "Charlie"),
                "Joining a full room should throw an exception");
        assertEquals("Room is full", ex.getMessage());
    }

    /**
     * Joining a room by the host's email should locate the room and add the
     * player.  Providing an unknown email should result in an
     * {@link IllegalArgumentException} containing the phrase "Room not found".
     */
    @Test
    void joinRoomByEmailFindsRoom() {
        RoomService roomService = new RoomService();
        RoomDTO dto = roomService.createRoom("Host", "host@game.com");

        // Join by host email
        RoomDTO updated = roomService.joinRoomByEmail("host@game.com", "Guest");
        assertEquals(2, updated.getPlayers().size(), "Room should have two players after joining by email");
        assertEquals(dto.getCode(), updated.getCode(), "Room code should remain unchanged");

        // Non‑existent email
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> roomService.joinRoomByEmail("none@game.com", "Test"),
                "Joining with an unknown host email should throw an exception");
        assertTrue(ex.getMessage().contains("Room not found"));
    }

    /**
     * Starting a round should initialise the round with placeholder prompts,
     * set the room state to "in_round" and leave the round unrevealed.  The
     * returned {@link RoundDTO} should reflect these values.
     */
    @Test
    void startRoundInitialisesRound() {
        RoomService roomService = new RoomService();
        RoomDTO dto = roomService.createRoom("Host", "host@example.com");

        RoundDTO round = roomService.startRound(dto.getCode());
        assertNotNull(round, "startRound should return a non‑null RoundDTO");
        assertEquals("Froid", round.getPromptLeft(), "Prompt left should be the placeholder value");
        assertEquals("Chaud", round.getPromptRight(), "Prompt right should be the placeholder value");
        assertFalse(round.isRevealed(), "New rounds should not be revealed");

        RoomDTO state = roomService.getRoomState(dto.getCode());
        assertEquals("in_round", state.getState(), "Room state should be updated to in_round");
        assertNotNull(state.getCurrentRound(), "Current round on the room DTO should not be null");
    }

    /**
     * Codes generated by {@link RoomService#createRoom(String, String)} should
     * be unique across multiple invocations.  This test creates many rooms and
     * asserts that all codes are distinct.
     */
    @Test
    void generatedCodesAreUnique() {
        RoomService service = new RoomService();
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            RoomDTO dto = service.createRoom("Host" + i, "host" + i + "@example.com");
            assertTrue(codes.add(dto.getCode()), "Duplicate room code detected");
        }
        assertEquals(100, codes.size(), "All room codes should be unique");
    }
}