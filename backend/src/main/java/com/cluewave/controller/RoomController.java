// ---------------------------------------------------------------------
// ROOM REST CONTROLLER
// Exposes HTTP endpoints for creating, joining, and managing game rooms.
// Delegates business logic to RoomService and returns DTO representations.
// ---------------------------------------------------------------------

package com.cluewave.controller;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import com.cluewave.service.RoomService;
import com.cluewave.auth.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public record JoinRequest(String playerName) {}
    public record JoinByEmailRequest(String email, String playerName) {}

    @PostMapping
    public RoomDTO createRoom(@AuthenticationPrincipal UserPrincipal principal) {
        String username = principal.getUser().getUsername();
        String email = principal.getUser().getEmail();
        return roomService.createRoom(username, email);
    }

    @PostMapping("/{code}/join")
    public PlayerDTO joinRoom(@PathVariable String code, @RequestBody JoinRequest req,
                              @AuthenticationPrincipal UserPrincipal principal) {
        String name = req.playerName() != null && !req.playerName().isBlank()
                ? req.playerName() : principal.getUser().getUsername();
        return roomService.joinRoom(code, name);
    }

    @PostMapping("/join-by-email")
    public RoomDTO joinByEmail(@RequestBody JoinByEmailRequest req,
                               @AuthenticationPrincipal UserPrincipal principal) {
        String name = req.playerName() != null && !req.playerName().isBlank()
                ? req.playerName() : principal.getUser().getUsername();
        return roomService.joinRoomByEmail(req.email(), name);
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