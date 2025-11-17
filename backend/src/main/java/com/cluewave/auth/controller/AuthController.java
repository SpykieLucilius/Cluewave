// ---------------------------------------------------------------------
// AUTHENTICATION REST CONTROLLER
// Handles registration, login (email/password and social), and profile updates.
// Validates requests and wraps service responses in appropriate HTTP statuses.
// ---------------------------------------------------------------------

package com.cluewave.auth.controller;

import com.cluewave.auth.dto.AuthResponse;
import com.cluewave.auth.dto.LoginRequest;
import com.cluewave.auth.dto.RegisterRequest;
import com.cluewave.auth.dto.UpdateUserRequest;
import com.cluewave.auth.dto.UserDTO;
import com.cluewave.auth.dto.SocialLoginRequest;
import com.cluewave.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cluewave.auth.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

// ---------------------------------------------------------------------
// SOCIAL LOGIN (Google) — FEATURE INACTIVE
// Endpoint implemented but NOT used because it requires a valid Google
// OAuth client_id. A correct client_id cannot be generated without
// enabling Google Cloud billing, which is currently not possible.
// ---------------------------------------------------------------------

    @PostMapping("/social-login")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        try {
            AuthResponse response = authService.socialLogin(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        try {
            UserDTO updated = authService.updateUser(
                    principal.getUser().getId(),
                    request.getCurrentPassword(),
                    request.getUsername(),
                    request.getEmail(),
                    request.getNewPassword());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

}
