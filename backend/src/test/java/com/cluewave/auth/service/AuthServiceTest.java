package com.cluewave.auth.service;

import com.cluewave.auth.dto.AuthResponse;
import com.cluewave.auth.dto.LoginRequest;
import com.cluewave.auth.dto.RegisterRequest;
import com.cluewave.auth.dto.UserDTO;
import com.cluewave.auth.model.User;
import com.cluewave.auth.repository.UserRepository;
import com.cluewave.auth.security.JwtUtils;
import com.cluewave.auth.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests for {@link AuthService}.  These tests use Mockito to isolate
 * {@code AuthService} from its dependencies and verify the correct behaviour
 * for registration, authentication and profile updates.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtils jwtUtils;
    @InjectMocks
    AuthService authService;

    @Test
    void registerCreatesUser() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@example.com");
        req.setUsername("user");
        req.setPassword("secret");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(jwtUtils.generateToken(any(UserPrincipal.class))).thenReturn("jwt-token");
        when(jwtUtils.getJwtExpirationMs()).thenReturn(3600L);

        AuthResponse response = authService.register(req);
        assertNotNull(response, "Response should not be null");
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("user", response.getUser().getUsername());

        // capture saved user
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("user@example.com", saved.getEmail());
        assertEquals("user", saved.getUsername());
        verify(passwordEncoder).encode("secret");
    }

    @Test
    void registerFailsIfEmailTaken() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@example.com");
        req.setUsername("user");
        req.setPassword("secret");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void authenticateReturnsToken() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("secret");
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setUsername("user");
        user.setPassword("hashed");
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateToken(principal)).thenReturn("jwt-token");
        when(jwtUtils.getJwtExpirationMs()).thenReturn(3600L);

        AuthResponse response = authService.authenticate(req);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("user", response.getUser().getUsername());
    }

    @Test
    void updateUserValidations() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("old@example.com");
        user.setUsername("oldUser");
        user.setPassword("hashed");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // current password required
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, null, null, null, null));

        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, "wrong", null, null, null));

        // valid current password
        when(passwordEncoder.matches("correct", "hashed")).thenReturn(true);

        // invalid username length
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, "correct", "ab", null, null));

        // duplicate username
        when(userRepository.existsByUsername("newUser")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, "correct", "newUser", null, null));

        // prepare for email checks
        when(userRepository.existsByUsername("validName")).thenReturn(false);

        // invalid email format
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, "correct", "validName", "invalid-email", null));

        // duplicate email
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, "correct", "validName", "new@example.com", null));

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        // invalid password length
        assertThrows(IllegalArgumentException.class, () -> authService.updateUser(userId, "correct", "validName", null, "123"));

        // success case
        when(passwordEncoder.matches("correct", "hashed")).thenReturn(true);
        when(userRepository.existsByUsername("validName")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashed");

        UserDTO updated = authService.updateUser(userId, "correct", "validName", "new@example.com", "newPassword");
        assertEquals("validName", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
        verify(userRepository, atLeastOnce()).save(user);
    }
}