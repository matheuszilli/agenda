package com.agenda.app.repository;

import com.agenda.app.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.app.model.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
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

    @EntityGraph(attributePaths = {"customer", "professional", "businessService", "subsidiary"})
    Page<Appointment> findAll(Pageable pageable);

    List<Appointment> findByProfessionalIdAndStartTimeAfterAndEndTimeBefore(
            UUID professionalId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<Appointment> findByCustomerIdAndStartTimeBetween(
            UUID customerId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     *  Lista de agendamentos IN_PROGESS e COMPLETED sem prontuario
     */
    @Query("SELECT a FROM Appointment a WHERE " +
            "(a.status = com.agenda.app.model.AppointmentStatus.ATTENDING " +
            "OR a.status = com.agenda.app.model.AppointmentStatus.COMPLETED) " +
            "AND a.serviceOrder IS NOT NULL " +
            "AND a.id NOT IN (SELECT mr.appointment.id FROM MedicalRecord mr)")
    List<Appointment> findAppointmentsWithoutMedicalRecord();



    /**
     * Pesquisa agendamento com status especificos
     */

    @EntityGraph(attributePaths = {"customer"})
    List<Appointment> findByStatusAndSubsidiaryId(AppointmentStatus status, UUID subsidiaryId);

    /**
     * Listar clientes com status PENDING e startTime dentro dos próximos 2 dias
     */

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = com.agenda.app.model.AppointmentStatus.PENDING " +
            "AND a.startTime BETWEEN :now AND :nowPlus2days")
    List<Appointment> findPendingAppointmentsWithin2days(
            @Param("now") LocalDateTime now,
            @Param("nowPlus2days") LocalDateTime nowPlus2days
    );




    @Query("""
    SELECT COUNT(a) > 0
      FROM Appointment a
     WHERE a.professional.id = :professionalId
       AND a.subsidiary.id  <> :subsidiaryId
       AND a.startTime      <  :end
       AND a.endTime        >  :start
""")
    boolean existsOverlapInOtherSubsidiary(UUID professionalId,
                                           UUID subsidiaryId,
                                           LocalDateTime start,
                                           LocalDateTime end);


    @Query("""
        SELECT a
          FROM Appointment a
         WHERE a.status = com.agenda.app.model.AppointmentStatus.PENDING
           AND a.businessService.requiresPrePayment = true
           AND a.startTime BETWEEN :now AND :deadline
    """)
    List<Appointment> findPendingAppointmentsWithin2Days(@Param("now")      LocalDateTime now,
                                                         @Param("deadline") LocalDateTime deadline);
}



