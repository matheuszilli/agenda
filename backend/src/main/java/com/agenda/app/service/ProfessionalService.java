// src/main/java/com/agenda/app/service/ProfessionalService.java
package com.agenda.app.service;

import com.agenda.app.dto.*;
import com.agenda.app.mapper.ItemMapper;
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
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final UserRepository userRepository; // se for usar userId
    private final ProfessionalMapper mapper;
    private final ItemRepository itemRepository;
    private final ProfessionalServiceCfgMapper cfgMapper;
    private final ItemMapper itemMapper;

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
                ProfessionalServiceCfg cfg = cfgMapper.toEntity(cfgReq, itemRepository); // injete esse repo no service

                /* seta o FK para o professional recém-criado */
                cfg.setProfessional(entity);

                /* adiciona na coleção do Professional */
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

    public List<ItemResponse> getServicesByProfessional(UUID professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        return professional.getServices().stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /* =========== UPDATE =========== */
    @Transactional
    public ProfessionalResponse update(UUID id, ProfessionalRequest dto) {
        
        System.out.println("=== UPDATE PROFESSIONAL ===");
        System.out.println("ID: " + id);
        System.out.println("DTO: " + dto);
        System.out.println("Services: " + dto.getServices());

        Professional entity = professionalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));

        mapper.copyNonNullToEntity(dto, entity);

        // se receber outro subsidiaryId, atualiza:
        if (dto.getSubsidiaryId() != null && !dto.getSubsidiaryId().equals(entity.getSubsidiary().getId())) {
            Subsidiary newSubs = subsidiaryRepository.findById(dto.getSubsidiaryId())
                    .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));
            entity.setSubsidiary(newSubs);
        }

        /* ======= ATUALIZAR CONFIGURAÇÕES DE SERVIÇO ======= */
        if (dto.getServices() != null) {
            System.out.println("=== PROCESSANDO CONFIGURAÇÕES DE SERVIÇO ===");
            System.out.println("Número de serviços: " + dto.getServices().size());
            
            // Primeiro, salvar o professional para garantir que está persistido
            professionalRepository.save(entity);
            
            // Limpar configurações existentes usando query direta
            entity.getServiceConfigs().clear();
            professionalRepository.flush(); // Força a sincronização
            
            // Adicionar novas configurações uma por uma
            for (ProfessionalServiceCfgRequest cfgReq : dto.getServices()) {
                System.out.println("Processando configuração: " + cfgReq);
                
                try {
                    // Buscar o Item (serviço) manualmente para garantir que está gerenciado
                    Item service = itemRepository.findById(cfgReq.serviceId())
                            .orElseThrow(() -> new IllegalArgumentException("Service not found: " + cfgReq.serviceId()));
                    
                    System.out.println("Serviço encontrado: " + service.getName());
                    
                    // Criar configuração manualmente
                    ProfessionalServiceCfg cfg = new ProfessionalServiceCfg();
                    cfg.setProfessional(entity);
                    cfg.setService(service);
                    cfg.setCustomPrice(cfgReq.customPrice());
                    cfg.setCustomDurationMinutes(cfgReq.customDurationMinutes());
                    cfg.setCommissionPct(cfgReq.commissionPct());
                    cfg.setCommissionFixed(cfgReq.commissionFixed());
                    
                    /* adiciona diretamente na coleção */
                    entity.getServiceConfigs().add(cfg);
                    
                    System.out.println("Configuração criada com sucesso");
                } catch (Exception e) {
                    System.err.println("Erro ao processar configuração: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            System.out.println("Configurações atualizadas com sucesso");
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

    public List<ProfessionalResponse> listBySubsidiary(UUID subsidiaryId) {
        return professionalRepository.findBySubsidiaryId(subsidiaryId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /* =========== DELETE =========== */
    @Transactional
    public void delete(UUID id) {
        professionalRepository.deleteById(id);
    }
}