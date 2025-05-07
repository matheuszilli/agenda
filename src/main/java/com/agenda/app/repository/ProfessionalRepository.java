package com.agenda.app.repository;

import com.agenda.app.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface de acesso a dados para a entidade Professional.
 * Oferece operações CRUD básicas e pode ser estendida para consultas customizadas.
 */
@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {

}