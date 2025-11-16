package com.cluewave.controller;

import com.cluewave.dto.PlayerDTO;
import com.cluewave.dto.RoomDTO;
import com.cluewave.dto.RoundDTO;
import com.cluewave.service.RoomService;
import com.cluewave.auth.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing endpoints to create and join rooms.  This
 * controller relies on Spring Security to inject the authenticated user via
 * {@link AuthenticationPrincipal}.  Requests to these endpoints must include
 * a valid JWT token; otherwise the security filter chain will reject them.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Request body for joining a room by code.  The playerName field is
     * optional; if absent or blank the authenticated user’s username is used.
     */
    public record JoinRequest(String playerName) {}

    /**
     * Request body for joining a room by host email.  The email is required
     * and playerName is optional; if omitted the user’s username is used.
     */
    public record JoinByEmailRequest(String email, String playerName) {}

    /**
     * Creates a new game room for the authenticated user.  No request body is
     * required; the user’s username and email are used for the host details.
     *
     * @param principal the authenticated user principal
     * @return the created room as a DTO
     */
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@AuthenticationPrincipal UserPrincipal principal) {
        try {
            String hostName = principal.getUser().getUsername();
            String hostEmail = principal.getUser().getEmail();
            RoomDTO dto = roomService.createRoom(hostName, hostEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception ex) {
            // Should not happen under normal circumstances
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds the authenticated user to a room by code.  If the playerName is
     * provided in the body it is used; otherwise the user’s username is used.
     *
     * @param code      the room code path parameter
     * @param req       request containing optional playerName
     * @param principal the authenticated user principal
     * @return the created player DTO or an error response if the room is full
     */
    @PostMapping("/{code}/join")
    public ResponseEntity<?> joinRoom(@PathVariable String code,
                                      @RequestBody(required = false) JoinRequest req,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        String playerName = (req != null && req.playerName() != null && !req.playerName().isBlank())
                ? req.playerName()
                : principal.getUser().getUsername();
        try {
            PlayerDTO playerDTO = roomService.joinRoom(code, playerName);
            return ResponseEntity.ok(playerDTO);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Joins a room by searching for the host’s email.  The email must be
     * provided; playerName is optional and defaults to the user’s username.
     *
     * @param req       body containing email and optional playerName
     * @param principal the authenticated user principal
     * @return the updated RoomDTO or an error response
     */
    @PostMapping("/join-by-email")
    public ResponseEntity<?> joinByEmail(@RequestBody JoinByEmailRequest req,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        String playerName = (req.playerName() != null && !req.playerName().isBlank())
                ? req.playerName()
                : principal.getUser().getUsername();
        try {
            RoomDTO dto = roomService.joinRoomByEmail(req.email(), playerName);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /**
     * Retrieves the current state of a room by code.
     */
    @GetMapping("/{code}")
    public ResponseEntity<?> getRoom(@PathVariable String code) {
        try {
            return ResponseEntity.ok(roomService.getRoomState(code));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Starts a new round in the specified room.
     */
    @PostMapping("/{code}/start-round")
    public ResponseEntity<?> startRound(@PathVariable String code) {
        try {
            RoundDTO roundDTO = roomService.startRound(code);
            return ResponseEntity.ok(roundDTO);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}