package com.cluewave.auth.service;

import com.cluewave.auth.dto.AuthResponse;
import com.cluewave.auth.dto.LoginRequest;
import com.cluewave.auth.dto.RegisterRequest;
import com.cluewave.auth.dto.UserDTO;
import com.cluewave.auth.model.User;
import com.cluewave.auth.repository.UserRepository;
import com.cluewave.auth.security.JwtUtils;
import com.cluewave.auth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encapsulates the authentication and registration logic.  This service
 * interacts with the database via {@link UserRepository}, encodes passwords
 * using {@link PasswordEncoder} and issues JWTs via {@link JwtUtils}.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Registers a new user.  Throws IllegalArgumentException if the username or
     * email already exist.  Passwords are encoded prior to persistence.
     *
     * @param request request containing username, email and plain password
     * @return AuthResponse with a JWT and basic user info
     */
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

    /**
     * Authenticates a user using the Spring Security {@link AuthenticationManager}.
     * If authentication is successful a JWT is generated.  Otherwise an
     * exception is propagated back to the controller layer.
     *
     * @param request request containing email and password
     * @return AuthResponse with a JWT and basic user info
     */
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
