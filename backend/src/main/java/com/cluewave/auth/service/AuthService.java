// ---------------------------------------------------------------------
// AUTHENTICATION SERVICE
// Provides user registration, email/password login, Google social login,
// JWT generation/validation, and profile update logic with validations.
// Interacts with UserRepository, PasswordEncoder, AuthenticationManager, and JwtUtils.
// ---------------------------------------------------------------------

package com.cluewave.auth.service;

import com.cluewave.auth.dto.AuthResponse;
import com.cluewave.auth.dto.LoginRequest;
import com.cluewave.auth.dto.RegisterRequest;
import com.cluewave.auth.dto.UserDTO;
import com.cluewave.auth.dto.SocialLoginRequest;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

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
    public AuthResponse socialLogin(SocialLoginRequest request) {
        if (request == null || request.getProvider() == null || request.getIdToken() == null) {
            throw new IllegalArgumentException("Invalid social login request");
        }

        String provider = request.getProvider().trim().toLowerCase();
        String idToken = request.getIdToken().trim();
        if (provider.isEmpty() || idToken.isEmpty()) {
            throw new IllegalArgumentException("Provider and idToken are required");
        }

        if (!"google".equals(provider)) {
            throw new IllegalArgumentException("Unsupported social provider: " + provider);
        }

        String email;
        String displayName;
        try {
            String encoded = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
            URI uri = URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + encoded);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            InputStream is = conn.getInputStream();
            JsonNode json = new ObjectMapper().readTree(is);

            JsonNode emailNode = json.get("email");
            if (emailNode == null || emailNode.asText().isBlank()) {
                throw new IllegalArgumentException("Google token does not contain an email");
            }
            email = emailNode.asText();

            JsonNode nameNode = json.get("name");
            if (nameNode != null && !nameNode.asText().isBlank()) {
                displayName = nameNode.asText();
            } else {
                int atIdx = email.indexOf('@');
                displayName = atIdx > 0 ? email.substring(0, atIdx) : email;
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to verify Google ID token");
        }

        Optional<User> existingOpt = userRepository.findByEmail(email);
        User user;
        if (existingOpt.isPresent()) {
            user = existingOpt.get();
        } else {
            String baseUsername = displayName.replaceAll("\\s+", "").toLowerCase();
            String username = generateUniqueUsername(baseUsername);

            user = new User();
            user.setEmail(email);
            user.setUsername(username);

            String randomPassword = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(randomPassword));
            userRepository.save(user);
        }

        UserPrincipal principal = new UserPrincipal(user);
        String jwt = jwtUtils.generateToken(principal);
        return new AuthResponse(jwt, "Bearer", jwtUtils.getJwtExpirationMs(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
    }

    private String generateUniqueUsername(String base) {
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    @Transactional
    public UserDTO updateUser(Long userId,
                              String currentPassword,
                              String username,
                              String email,
                              String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        if (username != null && !username.isBlank() && !username.equals(user.getUsername())) {
            if (username.length() < 3) {
                throw new IllegalArgumentException("Username must be at least 3 characters");
            }
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(username);
        }

        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            Pattern emailRegex = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            if (!emailRegex.matcher(email).matches()) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(email);
        }

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
