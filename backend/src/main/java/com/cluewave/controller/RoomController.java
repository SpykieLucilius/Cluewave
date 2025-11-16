package com.cluewave.controller;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import com.cluewave.service.RoomService;
import com.cluewave.auth.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
     * Payload for joining a room by code.  A playerName is provided by the client and
     * will be used as the display name when the player joins.  If the name is blank
     * the service will fall back to the authenticated user’s username.
     */
    public record JoinRequest(String playerName) {}
    /**
     * Payload for joining a room by searching for the host’s email.  The email
     * field must be provided and a playerName may be given; if omitted the
     * authenticated user’s username will be used.
     */
    public record JoinByEmailRequest(String email, String playerName) {}

    /**
     * Creates a new room for the authenticated user.  The host’s username and email
     * are obtained from the authentication principal.  No request body is
     * required.
     *
     * @param principal the authenticated user principal injected by Spring
     * @return the newly created RoomDTO
     */
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