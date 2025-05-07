package com.agenda.app.service;

import com.agenda.app.model.Appointment;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.model.Professional;
import com.agenda.app.repository.AppointmentRepository;
import com.agenda.app.repository.SubsidiaryRepository;
import com.agenda.app.repository.ProfessionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.UUID;

@Service
public class UtilizationService {

    private final AppointmentRepository apptRepo;
    private final SubsidiaryRepository subRepo;
    private final ProfessionalRepository profRepo;

    public UtilizationService(AppointmentRepository apptRepo,
                              SubsidiaryRepository subRepo,
                              ProfessionalRepository profRepo) {
        this.apptRepo = apptRepo;
        this.subRepo = subRepo;
        this.profRepo = profRepo;
    }

    @Transactional(readOnly = true)
    public UnitUtilizationDTO calculateUnitUtilization(UUID subsidiaryId, LocalDate date) {
        Subsidiary sub = subRepo.findById(subsidiaryId)
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));

        // **Passo 2**: buscar todos os appointments daquela filial no intervalo [open, close]
        LocalDateTime startOfDay = date.atTime(sub.getOpenTime());
        LocalDateTime endOfDay   = date.atTime(sub.getCloseTime());
        List<Appointment> all = apptRepo.findBySubsidiaryIdAndStartTimeBetween(
                subsidiaryId, startOfDay, endOfDay
        );

        // **Passo 3**: calcula capacidade e tempo agendado
        long capacity = Duration.between(sub.getOpenTime(), sub.getCloseTime()).toMinutes();
        long booked   = all.stream()
                .mapToLong(a -> Duration.between(a.getStartTime(), a.getEndTime()).toMinutes())
                .sum();

        double utilizationPct = (double) booked / capacity * 100.0;
        return new UnitUtilizationDTO(date, utilizationPct, booked, capacity);
    }

    @Transactional(readOnly = true)
    public ProfessionalUtilizationDTO calculateProfessionalUtilization(UUID professionalId, LocalDate date) {
        Professional prof = profRepo.findById(professionalId)
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));
        Subsidiary sub = prof.getSubsidiary(); // pressupõe que exista esse relacionamento

        // interseção dos horários da filial e do profissional
        LocalTime start = sub.getOpenTime().isAfter(prof.getAvailableStart())
                ? sub.getOpenTime() : prof.getAvailableStart();
        LocalTime end   = sub.getCloseTime().isBefore(prof.getAvailableEnd())
                ? sub.getCloseTime() : prof.getAvailableEnd();

        List<Appointment> apps = apptRepo.findByProfessionalIdAndStartTimeBetween(
                professionalId,
                date.atTime(start),
                date.atTime(end)
        );

        long capacity = Duration.between(start, end).toMinutes();
        long booked   = apps.stream()
                .mapToLong(a -> Duration.between(a.getStartTime(), a.getEndTime()).toMinutes())
                .sum();

        double utilizationPct = capacity == 0 ? 0.0 : ((double) booked / capacity * 100.0);
        return new ProfessionalUtilizationDTO(date, professionalId, utilizationPct, booked, capacity);
    }

    public record UnitUtilizationDTO(
            LocalDate date, double utilizationPct, long bookedMinutes, long capacityMinutes
    ) {}

    public record ProfessionalUtilizationDTO(
            LocalDate date, UUID professionalId, double utilizationPct,
            long bookedMinutes, long capacityMinutes
    ) {}
}