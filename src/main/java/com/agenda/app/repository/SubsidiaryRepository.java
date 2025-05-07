package com.agenda.app.repository;

import com.agenda.app.model.Subsidiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface de acesso a dados para a entidade Subsidiary (unidade/filial).
 * Oferece operações CRUD básicas e pode ser estendida para consultas customizadas.
 */
@Repository
public interface SubsidiaryRepository extends JpaRepository<Subsidiary, UUID> {
}