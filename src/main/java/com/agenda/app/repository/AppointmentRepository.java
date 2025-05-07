package com.agenda.app.repository;

import com.agenda.app.model.AppointmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.app.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio naturalmente ja traz a seguinte lista
 * FindById
 * DeleteById
 *
 */


public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByProfessionalIdAndStartTimeAfterAndEndTimeBefore(
            UUID professionalId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<Appointment> findByCustomerIdAndStartTimeBetween(UUID customerId, LocalDateTime start, LocalDateTime end);


    @EntityGraph(attributePaths = {"customer"})
    List<Appointment> findByStatusAndSubsidiaryId(AppointmentStatus status, UUID subsidiaryId);


}
