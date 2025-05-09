package com.agenda.app.repository;

import com.agenda.app.model.MedicalRecord;
import com.agenda.app.model.Appointment;
import com.agenda.app.model.Professional;
import com.agenda.app.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    @EntityGraph(attributePaths = {"customer", "professional", "notes"})

    Optional<MedicalRecord> findByAppointment(
            Appointment appointment
    );

    List<MedicalRecord> findByCustomerId(
            UUID customerId
    );

    List<MedicalRecord> findByAppointmentAndCustomerId(
            Appointment appointment,
            UUID customerId
    );

    Optional<MedicalRecord> findByAppointmentId(
            UUID appointmentId
    );

    List<MedicalRecord> findByFinalizedFalse();

    List<MedicalRecord> findByMainDoctor_IdAndFinalizedFalse(
            UUID mainDoctorId
    );

    boolean existsByAppointment(Appointment appointment);
}
