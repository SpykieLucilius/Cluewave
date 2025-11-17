// ---------------------------------------------------------------------
// SOCIAL LOGIN REQUEST DTO
// Payload for initiating social authentication via a provider (e.g. Google).
// Includes provider name and provider-issued ID token required for verification.
// ---------------------------------------------------------------------

package com.cluewave.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class SocialLoginRequest {

    @NotBlank
    private String provider;

    @NotBlank
    private String idToken;

    public SocialLoginRequest() {
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
