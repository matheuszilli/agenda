package com.agenda.app.repository;

import com.agenda.app.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.app.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository<UUID> extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByCompany(Company company);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
