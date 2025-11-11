package com.cluewave.auth.dto;

/**
 * Data transfer object exposing non‑sensitive user information.  Do not include
 * the password or other confidential fields here.  This object is returned in
 * authentication responses to the frontend.
 */
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
