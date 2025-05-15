package com.agenda.app.repository;

import com.agenda.app.model.ChairRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChairRoomRepository extends JpaRepository<ChairRoom, UUID> {

    List<ChairRoom> findBySubsidiaryId(UUID subsidiaryId);

    List<ChairRoom> findBySubsidiaryIdAndIsAvailable(UUID subsidiaryId, boolean isAvailable);

    @Query("SELECT cr FROM ChairRoom cr WHERE cr.subsidiary.id = :subsidiaryId " +
            "AND cr.isAvailable = true " +
            "AND cr.id NOT IN (" +
            "    SELECT a.chairRoom.id FROM Appointment a " +
            "    WHERE a.subsidiary.id = :subsidiaryId " +
            "    AND (a.status = com.agenda.app.model.AppointmentStatus.CONFIRMED " +
            "         OR a.status = com.agenda.app.model.AppointmentStatus.ATTENDING) " +
            "    AND a.startTime < :endTime " +
            "    AND a.endTime > :startTime" +
            ")")
    List<ChairRoom> findAvailableRoomsForTimeSlot(
            @Param("subsidiaryId") UUID subsidiaryId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}