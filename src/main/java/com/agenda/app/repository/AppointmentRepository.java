package com.agenda.app.repository;

import com.agenda.app.model.Appointment;
import com.agenda.app.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    /**
     * Busca paginação de agendamentos de um profissional em um intervalo de datas.
     */
    Page<Appointment> findByProfessionalIdAndStartTimeBetween(
            UUID professionalId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    /**
     * Busca todos os agendamentos de um profissional em um intervalo
     */
    List<Appointment> findByProfessionalIdAndStartTimeBetween(
            UUID professionalId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Busca paginação de agendamentos de um cliente.
     */
    Page<Appointment> findByCustomerId(
            UUID customerId,
            Pageable pageable
    );


    /**
     * Busca paginação de agendamentos por status.
     */
    Page<Appointment> findByStatus(
            AppointmentStatus status,
            Pageable pageable
    );

    /**
     * Busca agendamentos futuros de uma empresa ordenados por data de início.
     */
    @Query("SELECT a FROM Appointment a WHERE a.company.id = :companyId AND a.startTime >= :now ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingByCompany(
            @Param("companyId") UUID companyId,
            @Param("now") LocalDateTime now
    );


    /**
     * Busca agendamentos horários de abertura e fechamento da unidade
     */
    List<Appointment> findBySubsidiaryIdAndStartTimeBetween(
            UUID subsidiaryId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Busca de horarios disponibilizados por um profissional a partir de hoje
     */
    List<Appointment> findByProfessionalIdAndStartTimeAfter(
            UUID professionalId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Identifica potenciais conflitos de horário para um profissional.
     */
    @Query("SELECT a FROM Appointment a WHERE a.professional.id = :professionalId " +
            "AND a.startTime < :end AND a.endTime > :start")
    List<Appointment> findConflictingAppointments(
            @Param("professionalId") UUID professionalId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Busca paginação de agendamentos por filial.
     */
    Page<Appointment> findBySubsidiaryId(UUID subsidiaryId, Pageable pageable);

    /**
     * Busca paginação de agendamentos por empresa.
     */
    Page<Appointment> findByCompanyId(UUID companyId, Pageable pageable);

    /**
     * Projeção de resumo de agendamento para uma visão enxuta.
     */
    @Query("SELECT a.id AS id, a.startTime AS startTime, a.endTime AS endTime, " +
            "a.customer.firstName AS customerFirstName, a.customer.lastName AS customerLastName " +
            "FROM Appointment a WHERE a.professional.id = :professionalId")
    List<AppointmentSummary> findSummaryByProfessional(@Param("professionalId") UUID professionalId);

    interface AppointmentSummary {
        UUID getId();
        LocalDateTime getStartTime();
        LocalDateTime getEndTime();
        String getCustomerFirstName();
        String getCustomerLastName();
    }
}
