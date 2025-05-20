package com.agenda.app.repository;

import com.agenda.app.model.SubsidiaryScheduleEntry;
import com.agenda.app.model.Subsidiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SubsidiaryScheduleEntryRepository extends JpaRepository<SubsidiaryScheduleEntry, UUID> {

    List<SubsidiaryScheduleEntry> findBySubsidiaryId(UUID subsidiaryId);

    List<SubsidiaryScheduleEntry> findBySubsidiaryIdAndDate(UUID subsidiaryId, LocalDate date);

    boolean existsBySubsidiaryIdAndDate(UUID subsidiaryId, LocalDate date);
}