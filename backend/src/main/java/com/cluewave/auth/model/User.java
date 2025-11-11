package com.cluewave.auth.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Persistent user entity representing a registered player in the system.
 *
 * <p>The table name is defined explicitly to avoid conflicts with the existing
 * in‑memory {@code com.cluewave.model.Player} class used for room state.
 * Users have unique email and username fields and a BCrypt hashed password.
 * A timestamp is captured at creation time for auditing.  Additional
 * attributes (e.g. roles, profile data) can be added later as needed.</p>
 */

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The password is stored as a BCrypt hash.  Never expose this value to
     * clients.  See {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
     * for encoding details.
     */

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
