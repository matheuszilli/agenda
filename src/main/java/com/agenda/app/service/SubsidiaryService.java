// src/main/java/com/agenda/app/service/SubsidiaryService.java
package com.agenda.app.service;

import com.agenda.app.dto.SubsidiaryRequest;
import com.agenda.app.dto.SubsidiaryResponse;
import com.agenda.app.mapper.SubsidiaryMapper;
import com.agenda.app.model.Company;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.repository.CompanyRepository;
import com.agenda.app.repository.SubsidiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubsidiaryService {

    private final SubsidiaryRepository subsidiaryRepository;
    private final CompanyRepository companyRepository;
    private final SubsidiaryMapper mapper;

    /* ======================= CREATE ======================= */
    @Transactional
    public SubsidiaryResponse create(SubsidiaryRequest dto) {

        if (subsidiaryRepository.existsByNameIgnoreCaseAndCompanyId(dto.name(), dto.companyId())) {
            throw new IllegalArgumentException("Subsidiary already exists");
        }

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Subsidiary entity = mapper.toEntity(dto, company);
        subsidiaryRepository.save(entity);

        return mapper.toResponse(entity);
    }

    /* ======================= READ ========================= */
    @Transactional(readOnly = true)
    public SubsidiaryResponse get(UUID id) {
        return subsidiaryRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));
    }

    /* ======================= UPDATE ======================= */
    @Transactional
    public SubsidiaryResponse update(UUID id, SubsidiaryRequest dto) {

        Subsidiary entity = subsidiaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));

        // se quiser impedir troca de empresa, não mexa em company aqui
        mapper.copyNonNullToEntity(dto, entity);
        subsidiaryRepository.save(entity);

        return mapper.toResponse(entity);
    }

    /* ======================= DELETE ======================= */
    @Transactional
    public void delete(UUID id) {
        Subsidiary entity = subsidiaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));
        subsidiaryRepository.delete(entity);
    }
}
