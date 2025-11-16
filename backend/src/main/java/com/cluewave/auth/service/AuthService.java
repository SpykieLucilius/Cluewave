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

    /**
     * Authenticates a user using a social provider.  Currently only Google
     * tokens are supported.  The method verifies the provided ID token with
     * the provider, extracts the user's email and name, creates a new
     * account if none exists, and returns a standard {@link AuthResponse}.
     *
     * @param request the request containing the provider name and ID token
     * @return a fully populated authentication response
     * @throws IllegalArgumentException if the token is invalid or the provider
     *                                  is unsupported
     */
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

        // Only Google is supported for now
        if (!"google".equals(provider)) {
            throw new IllegalArgumentException("Unsupported social provider: " + provider);
        }

        // Verify the ID token by calling Google's tokeninfo endpoint.
        String email;
        String displayName;
        try {
            String encoded = URLEncoder.encode(idToken, StandardCharsets.UTF_8);
            // ✅ Use URI.create(...).toURL() instead of new URL(String) to avoid the deprecated ctor
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

        // Check if a user with this email already exists. If yes, reuse it.
        Optional<User> existingOpt = userRepository.findByEmail(email);
        User user;
        if (existingOpt.isPresent()) {
            user = existingOpt.get();
        } else {
            // Generate a unique username based on the display name.
            String baseUsername = displayName.replaceAll("\\s+", "").toLowerCase();
            String username = generateUniqueUsername(baseUsername);

            user = new User();
            user.setEmail(email);
            user.setUsername(username);

            // Random password for social accounts
            String randomPassword = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(randomPassword));
            userRepository.save(user);
        }

        UserPrincipal principal = new UserPrincipal(user);
        String jwt = jwtUtils.generateToken(principal);
        return new AuthResponse(jwt, "Bearer", jwtUtils.getJwtExpirationMs(),
                new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
    }

    /**
     * Generates a unique username by checking the repository for conflicts.
     */
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
