// ---------------------------------------------------------------------
// UPDATE USER REQUEST DTO
// Payload for updating a user's profile (username, email, and password).
// Only non-null fields are applied; current password is required for changes.
// ---------------------------------------------------------------------

package com.cluewave.auth.dto;

public class UpdateUserRequest {
    private String username;
    private String email;
    private String currentPassword;
    private String newPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
