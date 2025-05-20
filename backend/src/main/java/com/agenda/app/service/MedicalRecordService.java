package com.agenda.app.service;

import com.agenda.app.model.MedicalRecord;
import com.agenda.app.model.User;
import com.agenda.app.repository.MedicalRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord createMedicalRecord(MedicalRecord newRecord, User currentUser){
        // validação de nulos
        require(newRecord.getAppointment(), "Appointment is required");
        require(newRecord.getAppointment().getMainDoctor(), "Main doctor is required");
        require(currentUser, "User is required");

        // Verifica se ja existe um prontuário para esse agendamento
        if (medicalRecordRepository.existsByAppointment(newRecord.getAppointment())){
            throw new IllegalStateException("Appointment already exists");
        }

        // Confirma se usuário é o médico principal do agendamento
        if (!newRecord.getAppointment().getMainDoctor().getId().equals(currentUser.getId())){
            throw new IllegalStateException("Appointment does not belong to the current user");
        }

        newRecord.setCreatedBy(currentUser);
        newRecord.setCustomer(newRecord.getAppointment().getCustomer());
        newRecord.setItem(newRecord.getAppointment().getItem());
        newRecord.setFinalized(false);

        return medicalRecordRepository.save(newRecord);
    }

    public MedicalRecord updateMedicalRecord(UUID recordId, MedicalRecord updatedData, String userId) {
        MedicalRecord existing = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Medical record not found"));

        if (existing.isFinalized()) {
            throw new IllegalStateException("Cannot update finalized medical record");
        }

        // Acumula descrição
        String existingDescription = existing.getDescription() != null ? existing.getDescription() : "";
        String newDescription = updatedData.getDescription() != null ? updatedData.getDescription() : "";
        existing.setDescription(existingDescription + "\n" + newDescription);

        // Acumula fotos
        if (updatedData.getPhotosBefore() != null) {
            existing.getPhotosBefore().addAll(updatedData.getPhotosBefore());
        }

        if (updatedData.getPhotosAfter() != null) {
            existing.getPhotosAfter().addAll(updatedData.getPhotosAfter());
        }

        // Rastreabilidade
        existing.setUpdatedBy(userId);
        existing.setUpdatedAt(java.time.Instant.now());

        return medicalRecordRepository.save(existing);
    }

    private void require(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
