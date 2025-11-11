package com.cluewave.auth.dto;

/**
 * Response body returned after successful authentication.  Contains a JWT
 * access token, the token type (typically "Bearer"), expiration time in
 * milliseconds, and a {@link UserDTO} with basic user information.
 */
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserDTO user;

    public AuthResponse(String accessToken, String tokenType, long expiresIn, UserDTO user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserDTO getUser() {
        return user;
    }
}
