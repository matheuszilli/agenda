package com.agenda.app.repository;

import com.agenda.app.model.Company;
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

public interface CompanyRepository extends JpaRepository<Company, UUID>{
}
