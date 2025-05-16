package com.agenda.app.service;

import com.agenda.app.dto.ProfessionalScheduleEntryRequest;
import com.agenda.app.dto.ProfessionalScheduleEntryResponse;
import com.agenda.app.mapper.ProfessionalScheduleEntryMapper;
import com.agenda.app.model.Professional;
import com.agenda.app.model.ProfessionalScheduleEntry;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.repository.ProfessionalRepository;
import com.agenda.app.repository.ProfessionalScheduleEntryRepository;
import com.agenda.app.repository.SubsidiaryScheduleEntryRepository;
import com.agenda.app.repository.ChairRoomScheduleEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalScheduleService {

    private final ProfessionalRepository professionalRepo;
    private final ProfessionalScheduleEntryRepository scheduleRepo;
    private final ProfessionalScheduleEntryMapper mapper;
    private final SubsidiaryScheduleEntryRepository subsidiaryScheduleRepo;

    @Transactional
    public ProfessionalScheduleEntryResponse create(ProfessionalScheduleEntryRequest dto) {
        Professional professional = professionalRepo.findById(dto.getProfessionalId())
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));

        // Verificar se já existe agendamento para esta data
        if (scheduleRepo.existsByProfessionalIdAndDate(dto.getProfessionalId(), dto.getDate())) {
            throw new IllegalArgumentException("Schedule already exists for this date");
        }

        // Verificar se o horário do profissional está dentro do horário da subsidiária
        Subsidiary subsidiary = professional.getSubsidiary();
        if (subsidiary != null) {
            var subsidiaryScheduleOpt = subsidiaryScheduleRepo
                    .findBySubsidiaryIdAndDate(subsidiary.getId(), dto.getDate())
                    .stream()
                    .findFirst();

            if (subsidiaryScheduleOpt.isPresent()) {
                var subsidiarySchedule = subsidiaryScheduleOpt.get();

                // Verificar se a subsidiária está fechada neste dia
                if (subsidiarySchedule.isClosed()) {
                    throw new IllegalArgumentException("Cannot schedule professional when subsidiary is closed");
                }

                // Verificar se o horário do profissional está dentro do horário da subsidiária
                if (dto.getStartTime().isBefore(subsidiarySchedule.getOpenTime()) ||
                        dto.getEndTime().isAfter(subsidiarySchedule.getCloseTime())) {
                    throw new IllegalArgumentException(
                            "Professional schedule must be within subsidiary hours: " +
                                    subsidiarySchedule.getOpenTime() + " - " + subsidiarySchedule.getCloseTime());
                }
            }
        }

        ProfessionalScheduleEntry entity = mapper.toEntity(dto, professional);
        scheduleRepo.save(entity);
        return mapper.toResponse(entity);
    }

    /**
     * Cria ou atualiza um agendamento para um profissional em uma data específica
     *
     * @param professionalId   ID do profissional
     * @param date             Data do agendamento
     * @param startTime        Horário de início
     * @param endTime          Horário de término
     * @param replaceExisting  Se deve substituir um agendamento existente
     * @return O agendamento criado/atualizado
     */
    @Transactional
    public ProfessionalScheduleEntryResponse createOrUpdateSchedule(
            UUID professionalId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            boolean replaceExisting) {

        Professional professional = professionalRepo.findById(professionalId)
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + professionalId));

        // Verificar se o horário do profissional está dentro do horário da subsidiária
        Subsidiary subsidiary = professional.getSubsidiary();
        if (subsidiary != null) {
            var subsidiaryScheduleOpt = subsidiaryScheduleRepo
                    .findBySubsidiaryIdAndDate(subsidiary.getId(), date)
                    .stream()
                    .findFirst();

            if (subsidiaryScheduleOpt.isPresent()) {
                var subsidiarySchedule = subsidiaryScheduleOpt.get();

                // Verificar se a subsidiária está fechada neste dia
                if (subsidiarySchedule.isClosed()) {
                    throw new IllegalArgumentException("Cannot schedule professional when subsidiary is closed");
                }

                // Verificar se o horário do profissional está dentro do horário da subsidiária
                if (startTime.isBefore(subsidiarySchedule.getOpenTime()) ||
                        endTime.isAfter(subsidiarySchedule.getCloseTime())) {
                    throw new IllegalArgumentException(
                            "Professional schedule must be within subsidiary hours: " +
                                    subsidiarySchedule.getOpenTime() + " - " + subsidiarySchedule.getCloseTime());
                }
            }
        }

        // Verificar se já existe um agendamento para esta data
        Optional<ProfessionalScheduleEntry> existing = scheduleRepo.findByProfessionalIdAndDate(professionalId, date);

        if (!existing.isEmpty()) {
            if (!replaceExisting) {
                throw new IllegalArgumentException("Schedule already exists for this date. Use replaceExisting=true to override.");
            }

            // Atualizar o agendamento existente
            ProfessionalScheduleEntry entry = existing.get();
            entry.setStartTime(startTime);
            entry.setEndTime(endTime);
            scheduleRepo.save(entry);
            return mapper.toResponse(entry);
        } else {
            // Criar um novo agendamento
            ProfessionalScheduleEntry entry = new ProfessionalScheduleEntry();
            entry.setProfessional(professional);
            entry.setDate(date);
            entry.setStartTime(startTime);
            entry.setEndTime(endTime);
            scheduleRepo.save(entry);
            return mapper.toResponse(entry);
        }
    }

    @Transactional
    public void delete(UUID id) {
        scheduleRepo.deleteById(id);
    }

    @Transactional
    public List<ProfessionalScheduleEntryResponse> listByProfessional(UUID professionalId) {
        return scheduleRepo.findByProfessionalId(professionalId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public List<ProfessionalScheduleEntryResponse> listByProfessionalAndDateRange(
            UUID professionalId, LocalDate startDate, LocalDate endDate) {
        // Aqui precisaria de um método personalizado no repository
        // Mas podemos filtrar manualmente por enquanto
        return scheduleRepo.findByProfessionalId(professionalId)
                .stream()
                .filter(entry -> !entry.getDate().isBefore(startDate) && !entry.getDate().isAfter(endDate))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfessionalScheduleEntryResponse getByProfessionalAndDate(UUID professionalId, LocalDate date) {
        return scheduleRepo.findByProfessionalIdAndDate(professionalId, date)
                .stream()
                .findFirst()
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("No schedule found for this professional on " + date));
    }
}