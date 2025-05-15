package com.agenda.app.service;

import com.agenda.app.dto.*;
import com.agenda.app.mapper.ProfessionalScheduleEntryMapper;
import com.agenda.app.model.*;
import com.agenda.app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessionalScheduleService {

    private final ProfessionalRepository professionalRepo;
    private final ProfessionalScheduleEntryRepository scheduleRepo;
    private final ProfessionalScheduleEntryMapper mapper;

    @Transactional
    public ProfessionalScheduleEntryResponse create(ProfessionalScheduleEntryRequest dto) {
        Professional professional = professionalRepo.findById(dto.getProfessionalId())
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));

        ProfessionalScheduleEntry entity = mapper.toEntity(dto, professional);
        scheduleRepo.save(entity);
        return mapper.toResponse(entity);
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
}