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
    Company findByName(String name);
    Optional<Company> findByDocumentNumber(String documentNumber);
    boolean existsByName(String name);
    boolean existsByDocumentNumber(String documentNumber);
    List<Company> findByNameContainingIgnoreCase(String name);
}
