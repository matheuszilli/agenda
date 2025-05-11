package com.agenda.app.service;


import com.agenda.app.dto.BusinessServiceRequest;
import com.agenda.app.dto.BusinessServiceResponse;
import com.agenda.app.mapper.BusinessServiceMapper;
import com.agenda.app.model.BusinessService;
import com.agenda.app.model.Company;
import com.agenda.app.repository.BusinessServiceRepository;
import com.agenda.app.repository.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessServiceService {

    private final BusinessServiceRepository businessServiceRepository;
    private final CompanyRepository companyRepository;
    private final BusinessServiceMapper mapper;

    // --- AQUI CRIA O SERVIÇO - AS REGRA PRA CRIAR E VALIDAR O SERVIÇO
    @Transactional
    public BusinessServiceResponse create(BusinessServiceRequest dto){

        if(businessServiceRepository.existsByNameIgnoreCaseAndCompanyId(dto.name(), dto.companyId())){
            throw new IllegalArgumentException("Service already exists");
        }

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        BusinessService entity = mapper.toEntity(dto);
        entity.setCompany(company);

        businessServiceRepository.save(entity);
        return mapper.toResponse(entity);
    }

    // --- AQUI LE O SERVIÇO
    @Transactional(readOnly = true)
    public BusinessServiceResponse get(UUID id) {
        return businessServiceRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

    // - UPDATE
    @Transactional
    public BusinessServiceResponse update(UUID id, BusinessServiceRequest dto) {
        BusinessService entity = businessServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        //sobrescrever campos
        mapper.toEntity(dto);
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setDurationMinutes(dto.durationMinutes());
        entity.setPrice(dto.price());
        entity.setActive(dto.active());
        entity.setRequiresPrePayment(dto.requiresPrePayment());

        businessServiceRepository.save(entity);
        return mapper.toResponse(entity);
    }

    // - DELETE
    @Transactional
    public void delete(UUID id) {
        BusinessService entity = businessServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        businessServiceRepository.delete(entity);
    }

}
