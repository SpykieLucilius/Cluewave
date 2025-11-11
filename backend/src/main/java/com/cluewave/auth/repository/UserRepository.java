package com.cluewave.auth.repository;

import com.cluewave.auth.model.User;
    import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * JPA repository providing CRUD operations for {@link User} entities.  The
 * existence checks and lookups by email or username are used during
 * registration and authentication.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
