package com.agenda.app.repository;

import com.agenda.app.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio naturalmente ja traz a seguinte lista
 * FindById
 * DeleteById
 *
 */

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {

    boolean existsByDocumentNumberIgnoreCase(String documentNumber);
    boolean existsByEmailIgnoreCase(String email);

    Optional<Professional> findByDocumentNumberIgnoreCase(String documentNumber);

    List<Professional> findBySubsidiaryId(UUID id);

    

}
