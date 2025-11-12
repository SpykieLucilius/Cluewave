package com.cluewave.controller;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import com.cluewave.service.RoomService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public record CreateRoomRequest(String hostName) {}

    public record JoinRequest(String playerName) {}

    @PostMapping
    public RoomDTO createRoom(@RequestBody CreateRoomRequest req) {
        return roomService.createRoom(req.hostName());
    }

    @PostMapping("/{code}/join")
    public PlayerDTO joinRoom(@PathVariable String code, @RequestBody JoinRequest req) {
        return roomService.joinRoom(code, req.playerName());
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