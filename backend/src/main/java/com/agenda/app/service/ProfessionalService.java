// src/main/java/com/agenda/app/service/ProfessionalService.java
package com.agenda.app.service;

import com.agenda.app.dto.*;
import com.agenda.app.mapper.ProfessionalMapper;
import com.agenda.app.mapper.ProfessionalServiceCfgMapper;
import com.agenda.app.model.*;
import com.agenda.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final SubsidiaryRepository   subsidiaryRepository;
    private final UserRepository         userRepository;      // se for usar userId
    private final ProfessionalMapper     mapper;
    private final ItemRepository itemRepository;
    private final ProfessionalServiceCfgMapper cfgMapper;

    /* =========== CREATE =========== */
    @Transactional
    public ProfessionalResponse create(ProfessionalRequest dto) {

        /* ----- validações simples ----- */
        if (professionalRepository.existsByDocumentNumberIgnoreCase(dto.getDocumentNumber()))
            throw new IllegalArgumentException("Document number already registered");

        if (professionalRepository.existsByEmailIgnoreCase(dto.getEmail()))
            throw new IllegalArgumentException("E-mail already registered");

        Subsidiary subsidiary = subsidiaryRepository.findById(dto.getSubsidiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));

        /* ----- converte ProfessionalRequest -> Professional ----- */
        Professional entity = mapper.toEntity(dto, subsidiary);

        /* ======= CONFIGURAÇÕES DE SERVIÇO ======= */
        if (dto.getServices() != null && !dto.getServices().isEmpty()) {
            for (ProfessionalServiceCfgRequest cfgReq : dto.getServices()) {

                /* cria ProfessionalServiceCfg a partir do DTO */
                ProfessionalServiceCfg cfg =
                        cfgMapper.toEntity(cfgReq, itemRepository);  // injete esse repo no service

                /* seta o FK para o professional recém-criado */
                cfg.setProfessional(entity);

                /* adiciona na coleção do Professional  */
                entity.getServiceConfigs().add(cfg);
            }
        }

        /* cascade = ALL garante que serviceConfigs também serão salvos */
        professionalRepository.save(entity);

        return mapper.toResponse(entity);
    }

    /* =========== READ =========== */
    @Transactional(readOnly = true)
    public ProfessionalResponse get(UUID id) {
        return professionalRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));
    }

    /* =========== UPDATE =========== */
    @Transactional
    public ProfessionalResponse update(UUID id, ProfessionalRequest dto) {

        Professional entity = professionalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));

        mapper.copyNonNullToEntity(dto, entity);

        // se receber outro subsidiaryId, atualiza:
        if (dto.getSubsidiaryId() != null && !dto.getSubsidiaryId().equals(entity.getSubsidiary().getId())) {
            Subsidiary newSubs = subsidiaryRepository.findById(dto.getSubsidiaryId())
                    .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));
            entity.setSubsidiary(newSubs);
        }

        professionalRepository.save(entity);
        return mapper.toResponse(entity);
    }

    /* =========== READ =========== */
    @Transactional(readOnly = true)
public List<ProfessionalResponse> listAll() {
    return professionalRepository.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
}

    /* =========== DELETE =========== */
    @Transactional
    public void delete(UUID id) {
        professionalRepository.deleteById(id);
    }
}