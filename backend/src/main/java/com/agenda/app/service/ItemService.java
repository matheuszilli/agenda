package com.agenda.app.service;


import com.agenda.app.dto.ItemRequest;
import com.agenda.app.dto.ItemResponse;
import com.agenda.app.mapper.ItemMapper;
import com.agenda.app.model.Item;
import com.agenda.app.model.Company;
import com.agenda.app.repository.ItemRepository;
import com.agenda.app.repository.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CompanyRepository companyRepository;
    private final ItemMapper mapper;

    // --- AQUI CRIA O SERVIÇO - AS REGRA PRA CRIAR E VALIDAR O SERVIÇO
    @Transactional
    public ItemResponse create(ItemRequest dto){

        if(itemRepository.existsByNameIgnoreCaseAndCompanyId(dto.name(), dto.companyId())){
            throw new IllegalArgumentException("Service already exists");
        }

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Item entity = mapper.toEntity(dto);
        entity.setCompany(company);

        itemRepository.save(entity);
        return mapper.toResponse(entity);
    }

    // --- AQUI LE O SERVIÇO
    @Transactional(readOnly = true)
    public ItemResponse get(UUID id) {
        return itemRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

    // - UPDATE
    @Transactional
    public ItemResponse update(UUID id, ItemRequest dto) {
        Item entity = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        //sobrescrever campos
        mapper.toEntity(dto);
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setDurationMinutes(dto.durationMinutes());
        entity.setPrice(dto.price());
        entity.setActive(dto.active());
        entity.setRequiresPrePayment(dto.requiresPrePayment());

        itemRepository.save(entity);
        return mapper.toResponse(entity);
    }

    // - DELETE
    @Transactional
    public void delete(UUID id) {
        Item entity = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        itemRepository.delete(entity);
    }

}
