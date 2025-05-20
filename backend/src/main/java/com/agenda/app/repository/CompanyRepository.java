package com.agenda.app.repository;

import com.agenda.app.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Verifica se existe alguma empresa com o mesmo prefixo de CNPJ
     * (excluindo a empresa com o ID especificado, se fornecido)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c " +
            "WHERE SUBSTRING(REPLACE(REPLACE(REPLACE(c.documentNumber, '.', ''), '/', ''), '-', ''), 1, 8) = " +
            "SUBSTRING(REPLACE(REPLACE(REPLACE(:cnpj, '.', ''), '/', ''), '-', ''), 1, 8) " +
            "AND (c.id <> :excludeId OR :excludeId IS NULL)")
    boolean existsByDocumentNumberPrefix(@Param("cnpj") String cnpj, @Param("excludeId") UUID excludeId);
}
