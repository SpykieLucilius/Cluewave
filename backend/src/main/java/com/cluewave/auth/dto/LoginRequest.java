// ---------------------------------------------------------------------
// LOGIN REQUEST DTO
// Payload sent by clients when performing standard email/password login.
// Contains validated email and password fields with bean validation annotations.
// ---------------------------------------------------------------------

package com.cluewave.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @Email(message = "Email address is not valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
