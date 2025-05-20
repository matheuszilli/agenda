package com.agenda.app.repository;

import com.agenda.app.model.MedicalRecordNote;
import com.agenda.app.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface MedicalRecordNoteRepository extends JpaRepository<MedicalRecordNote, UUID> {
//    List<MedicalRecordNote> findByProfessionalId (Professional professional);
//    List<MedicalRecordNote> findByCustomer_IdAndProfessional_IdAndServiceOrder_Id(
//            UUID customerId,
//            UUID professionalId,
//            UUID serviceOrderId);
}
