package com.cluewave.auth.security;

import com.cluewave.auth.model.User;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtUtils}.  These tests exercise token generation,
 * extraction and validation without relying on any external infrastructure.
 */
public class JwtUtilsTest {

    /**
     * Ensure that a token generated for a user can be validated and that
     * the username embedded in the token matches the user's email.  A base64
     * encoded secret is constructed from a 32‑byte string to meet the
     * requirements of the underlying signing implementation.
     */
    @Test
    void generateAndValidateToken() {
        // base64 encode a secret key of sufficient length
        String secret = Base64.getEncoder().encodeToString("secret-key-which-is-32byteslong!!".getBytes());
        long expirationMs = 60_000L;
        JwtUtils utils = new JwtUtils(secret, expirationMs);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("user");
        user.setPassword("pass");
        UserPrincipal principal = new UserPrincipal(user);

        String token = utils.generateToken(principal);
        assertNotNull(token, "Generated token should not be null");
        assertTrue(utils.validateToken(token), "Generated token should be valid");
        String username = utils.extractUsername(token);
        assertEquals("test@example.com", username, "Extracted username should match user email");
    }

    /**
     * Validating an arbitrary string should return false to indicate that the
     * token is malformed or invalid.
     */
    @Test
    void invalidTokenReturnsFalse() {
        String secret = Base64.getEncoder().encodeToString("another-secret-key-which-is-32-bytes".getBytes());
        JwtUtils utils = new JwtUtils(secret, 60_000L);
        assertFalse(utils.validateToken("invalid.token.value"), "Arbitrary tokens should not validate");
    }
}