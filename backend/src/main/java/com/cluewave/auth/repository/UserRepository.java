// ---------------------------------------------------------------------
// USER REPOSITORY INTERFACE
// Extends JpaRepository to perform CRUD operations on User entities.
// Declares query methods for finding users by email or username and existence checks.
// ---------------------------------------------------------------------

package com.cluewave.auth.repository;

import com.cluewave.auth.model.User;
    import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
