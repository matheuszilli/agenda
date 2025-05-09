package com.agenda.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.app.model.User;

import java.util.Optional;

public interface UserRepository<UUID> extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
