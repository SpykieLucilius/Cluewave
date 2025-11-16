package com.cluewave.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload used for social authentication.  The client should send
 * the name of the social provider (e.g. "google") along with the
 * provider‑issued ID token.  Both fields are required and validated
 * using {@link jakarta.validation.constraints.NotBlank} annotations.
 */
public class SocialLoginRequest {

    @NotBlank
    private String provider;

    @NotBlank
    private String idToken;

    public SocialLoginRequest() {
        // Default constructor for JSON deserialization
    }

    public SocialLoginRequest(String provider, String idToken) {
        this.provider = provider;
        this.idToken = idToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
