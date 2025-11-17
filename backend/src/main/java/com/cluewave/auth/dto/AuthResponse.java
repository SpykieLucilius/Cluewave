// ---------------------------------------------------------------------
// AUTHENTICATION RESPONSE DTO
// Encapsulates the JWT access token and user information returned after auth.
// Includes token type, expiration, and a nested UserDTO with id, username, and email.
// ---------------------------------------------------------------------

package com.cluewave.auth.dto;

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
