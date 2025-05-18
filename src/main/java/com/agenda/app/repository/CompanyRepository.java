package com.agenda.app.repository;

import com.agenda.app.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio naturalmente ja traz a seguinte lista
 * FindById
 * DeleteById
 *
 */

public interface CompanyRepository extends JpaRepository<Company, UUID>{
    boolean existsByName(String name);
    Company findByName(String name);
    List<Company> findByNameContainingIgnoreCase(String name);
}
