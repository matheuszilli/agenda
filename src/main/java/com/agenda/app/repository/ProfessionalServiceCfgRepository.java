package com.agenda.app.repository;

import com.agenda.app.model.ProfessionalServiceCfg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessionalServiceCfgRepository extends JpaRepository<ProfessionalServiceCfg, UUID> {

    Optional<ProfessionalServiceCfg> findByProfessionalIdAndServiceId(UUID professionalId, UUID serviceId);

    boolean existsByProfessionalIdAndServiceId(UUID professionalId, UUID serviceId);
}