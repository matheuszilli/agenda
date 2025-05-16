package com.agenda.app.repository;

import com.agenda.app.model.ChairRoomScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChairRoomScheduleEntryRepository extends JpaRepository<ChairRoomScheduleEntry, UUID> {

    List<ChairRoomScheduleEntry> findByChairRoomId(UUID chairRoomId);

    Optional<ChairRoomScheduleEntry> findByChairRoomIdAndDate(UUID chairRoomId, LocalDate date);

    boolean existsByChairRoomIdAndDate(UUID chairRoomId, LocalDate date);
}