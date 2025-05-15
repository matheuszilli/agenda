// src/main/java/com/agenda/app/service/CompanyService.java
package com.agenda.app.service;

import com.agenda.app.dto.*;
import com.agenda.app.mapper.CompanyMapper;
import com.agenda.app.model.Company;
import com.agenda.app.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repo;
    private final CompanyMapper mapper;

    /* CREATE */
    @Transactional
    public CompanyResponse create(CompanyRequest dto) {
        if (repo.existsByName(dto.name())) {
            throw new IllegalArgumentException("Company with name %s already exists".formatted(dto.name()));
        }
        Company entity = mapper.toEntity(dto);
        repo.save(entity);
        return mapper.toResponse(entity);
    }

    /* READ */
    @Transactional(readOnly = true)
    public CompanyResponse get(UUID id) {
        Company entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company %s not found".formatted(id)));
        return mapper.toResponse(entity);
    }
    @Transactional(readOnly = true)
    public List<CompanyResponse> getAll(String name) {
        if (name == null || name.isBlank()) {
            return mapper.toResponseList(repo.findAll());
        } else {
            return mapper.toResponseList(repo.findByNameContainingIgnoreCase(name));
        }
    }

    /* UPDATE (PUT) */
    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest dto) {
        Company entity = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company %s not found".formatted(id)));
        mapper.updateEntityFromDto(dto, entity);
        repo.save(entity);
        return mapper.toResponse(entity);
    }

    /* DELETE */
    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Company %s not found".formatted(id));
        }
        repo.deleteById(id);
    }
}
