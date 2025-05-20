package com.agenda.app.service;

import com.agenda.app.dto.SubsidiaryScheduleEntryRequest;
import com.agenda.app.dto.SubsidiaryScheduleEntryResponse;
import com.agenda.app.mapper.SubsidiaryScheduleEntryMapper;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.model.SubsidiaryScheduleEntry;
import com.agenda.app.repository.SubsidiaryRepository;
import com.agenda.app.repository.SubsidiaryScheduleEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubsidiaryScheduleEntryService {

    private final SubsidiaryRepository subsidiaryRepo;
    private final SubsidiaryScheduleEntryRepository entryRepo;
    private final SubsidiaryScheduleEntryMapper mapper;

    @Transactional
    public SubsidiaryScheduleEntryResponse create(SubsidiaryScheduleEntryRequest dto) {
        Subsidiary subsidiary = subsidiaryRepo.findById(dto.getSubsidiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));

        if (entryRepo.existsBySubsidiaryIdAndDate(subsidiary.getId(), dto.getDate())) {
            throw new IllegalArgumentException("Schedule already exists for this date");
        }

        SubsidiaryScheduleEntry entry = mapper.toEntity(dto, subsidiary);
        entryRepo.save(entry);
        return mapper.toResponse(entry);
    }

    /**
     * Cria ou atualiza um agendamento para uma subsidiária em uma data específica
     *
     * @param subsidiaryId     ID da subsidiária
     * @param date             Data do agendamento
     * @param openTime         Horário de abertura
     * @param closeTime        Horário de fechamento
     * @param closed           Se a subsidiária está fechada neste dia
     * @param replaceExisting  Se deve substituir um agendamento existente
     * @return O agendamento criado/atualizado
     */
    @Transactional
    public SubsidiaryScheduleEntryResponse createOrUpdateSchedule(
            UUID subsidiaryId,
            LocalDate date,
            LocalTime openTime,
            LocalTime closeTime,
            boolean closed,
            boolean replaceExisting) {

        Subsidiary subsidiary = subsidiaryRepo.findById(subsidiaryId)
                .orElseThrow(() -> new EntityNotFoundException("Subsidiary not found: " + subsidiaryId));

        // Verificar se já existe um agendamento para esta data
        Optional<SubsidiaryScheduleEntry> existingOpt = entryRepo.findBySubsidiaryIdAndDate(subsidiaryId, date)
                .stream()
                .findFirst();

        if (existingOpt.isPresent()) {
            if (!replaceExisting) {
                throw new IllegalArgumentException("Schedule already exists for this date. Use replaceExisting=true to override.");
            }

            // Atualizar o agendamento existente
            SubsidiaryScheduleEntry existing = existingOpt.get();
            existing.setOpenTime(openTime);
            existing.setCloseTime(closeTime);
            existing.setClosed(closed);
            existing.setCustomized(true);
            entryRepo.save(existing);
            return mapper.toResponse(existing);
        } else {
            // Criar um novo agendamento
            SubsidiaryScheduleEntry entry = new SubsidiaryScheduleEntry();
            entry.setSubsidiary(subsidiary);
            entry.setDate(date);
            entry.setOpenTime(openTime);
            entry.setCloseTime(closeTime);
            entry.setClosed(closed);
            entry.setCustomized(true);
            entryRepo.save(entry);
            return mapper.toResponse(entry);
        }
    }

    @Transactional
    public SubsidiaryScheduleEntryResponse update(UUID id, SubsidiaryScheduleEntryRequest dto) {
        SubsidiaryScheduleEntry entry = entryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        mapper.updateFromRequest(dto, entry);
        entry.setCustomized(true); // reforça personalização
        return mapper.toResponse(entryRepo.save(entry));
    }

    @Transactional
    public void delete(UUID id) {
        entryRepo.deleteById(id);
    }

    public List<SubsidiaryScheduleEntryResponse> listBySubsidiary(UUID subsidiaryId) {
        return entryRepo.findBySubsidiaryId(subsidiaryId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public SubsidiaryScheduleEntryResponse getByDate(UUID subsidiaryId, LocalDate date) {
        return entryRepo.findBySubsidiaryIdAndDate(subsidiaryId, date).stream()
                .findFirst()
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("No schedule for that date"));
    }
}