package com.agenda.app.repository;

import com.agenda.app.model.ProfessionalScheduleEntry;
import com.agenda.app.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProfessionalScheduleEntryRepository extends JpaRepository<ProfessionalScheduleEntry, UUID> {

    List<ProfessionalScheduleEntry> findByProfessionalId(UUID professionalId);

    List<ProfessionalScheduleEntry> findByProfessionalIdAndDate(UUID professionalId, LocalDate date);

    boolean existsByProfessionalIdAndDate(UUID professionalId, LocalDate date);
}