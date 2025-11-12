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
}
