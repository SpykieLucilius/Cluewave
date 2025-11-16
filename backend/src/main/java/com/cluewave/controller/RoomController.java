package com.cluewave.controller;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import com.cluewave.service.RoomService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing endpoints for room creation and joining.  Rooms can be
 * joined either by code or by host email.  Endpoints return DTOs for the
 * current room or player state.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public record CreateRoomRequest(String hostName, String hostEmail) {}
    public record JoinRequest(String playerName) {}
    public record JoinByEmailRequest(String email, String playerName) {}

    @PostMapping
    public RoomDTO createRoom(@RequestBody CreateRoomRequest req) {
        return roomService.createRoom(req.hostName(), req.hostEmail());
    }

    @PostMapping("/{code}/join")
    public PlayerDTO joinRoom(@PathVariable String code, @RequestBody JoinRequest req) {
        return roomService.joinRoom(code, req.playerName());
    }

    @PostMapping("/join-by-email")
    public RoomDTO joinByEmail(@RequestBody JoinByEmailRequest req) {
        return roomService.joinRoomByEmail(req.email(), req.playerName());
    }

    @GetMapping("/{code}")
    public RoomDTO getRoom(@PathVariable String code) {
        return roomService.getRoomState(code);
    }

    @PostMapping("/{code}/start-round")
    public RoundDTO startRound(@PathVariable String code) {
        return roomService.startRound(code);
    }
}