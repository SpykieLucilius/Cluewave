package com.cluewave.auth.service;

import com.cluewave.auth.dto.AuthResponse;
import com.cluewave.auth.dto.LoginRequest;
import com.cluewave.auth.dto.RegisterRequest;
import com.cluewave.auth.dto.UserDTO;
import com.cluewave.auth.model.User;
import com.cluewave.auth.repository.UserRepository;
import com.cluewave.auth.security.JwtUtils;
import com.cluewave.auth.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtils.generateToken(principal);
        return new AuthResponse(token, "Bearer", jwtUtils.getJwtExpirationMs(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
    }

    public AuthResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtils.generateToken(principal);
        User user = principal.getUser();
        return new AuthResponse(token, "Bearer", jwtUtils.getJwtExpirationMs(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
    }

    @Transactional
    public UserDTO updateUser(Long userId,
            String currentPassword,
            String username,
            String email,
            String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Le mot de passe actuel est obligatoire pour toute modification
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        // Vérifier que le mot de passe actuel correspond
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        // Mise à jour du nom d’utilisateur
        if (username != null && !username.isBlank() && !username.equals(user.getUsername())) {
            if (username.length() < 3) {
                throw new IllegalArgumentException("Username must be at least 3 characters");
            }
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(username);
        }

        // Mise à jour de l’email
        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            // Vérification basique du format d’email
            Pattern emailRegex = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            if (!emailRegex.matcher(email).matches()) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(email);
        }

        // Mise à jour du mot de passe
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }

}
