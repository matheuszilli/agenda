package com.agenda.app.service;

import com.agenda.app.dto.SubsidiaryScheduleEntryRequest;
import com.agenda.app.dto.SubsidiaryScheduleEntryResponse;
import com.agenda.app.mapper.SubsidiaryScheduleEntryMapper;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.model.SubsidiaryScheduleEntry;
import com.agenda.app.repository.SubsidiaryRepository;
import com.agenda.app.repository.SubsidiaryScheduleEntryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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