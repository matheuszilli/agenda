package com.agenda.app.service;

import com.agenda.app.dto.*;
import com.agenda.app.mapper.CompanyMapper;
import com.agenda.app.model.Company;
import com.agenda.app.repository.CompanyRepository;
import com.agenda.app.util.CnpjUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repo;
    private final CompanyMapper mapper;

    /* CREATE */
    @Transactional
    public CompanyResponse create(CompanyRequest dto) {
        // Validar nome único
        if (repo.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company with name %s already exists".formatted(dto.getName()));
        }

        // Formatar CNPJ para o padrão XX.XXX.XXX/XXXX-XX
        String formattedCnpj;
        try {
            formattedCnpj = CnpjUtils.formatCnpj(dto.getDocumentNumber());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CNPJ format: " + e.getMessage());
        }

        // Validar CNPJ único
        if (repo.existsByDocumentNumber(formattedCnpj)) {
            throw new IllegalArgumentException("Company with CNPJ %s already exists".formatted(formattedCnpj));
        }

        // Validar que não existe outra empresa com o mesmo prefixo de CNPJ
        if (repo.existsByDocumentNumberPrefix(formattedCnpj, null)) {
            throw new IllegalArgumentException("Another company with the same CNPJ prefix already exists. Only one company with the same root is allowed");
        }

        // Atualizar o CNPJ formatado no DTO
        dto.setDocumentNumber(formattedCnpj);

        // Criar a entidade e salvar
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

        // Verificar se o nome está sendo alterado e se já existe
        if (!dto.getName().equals(entity.getName()) && repo.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company with name %s already exists".formatted(dto.getName()));
        }

        // Formatar CNPJ
        String formattedCnpj;
        try {
            formattedCnpj = CnpjUtils.formatCnpj(dto.getDocumentNumber());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CNPJ format: " + e.getMessage());
        }

        // Verificar se o CNPJ está sendo alterado e se já existe
        if (!formattedCnpj.equals(entity.getDocumentNumber()) &&
                repo.existsByDocumentNumber(formattedCnpj)) {
            throw new IllegalArgumentException("Company with CNPJ %s already exists".formatted(formattedCnpj));
        }

        // Verificar se o CNPJ está sendo alterado para um prefixo que já existe em outra empresa
        if (!formattedCnpj.equals(entity.getDocumentNumber()) &&
                repo.existsByDocumentNumberPrefix(formattedCnpj, id)) {
            throw new IllegalArgumentException("Another company with the same CNPJ prefix already exists. Only one company with the same root is allowed");
        }

        // Atualizar o CNPJ formatado no DTO
        dto.setDocumentNumber(formattedCnpj);

        // Atualizar a entidade
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

    /**
     * Verifica se um CNPJ pertence à mesma empresa que um CNPJ de referência
     * Útil para validar se uma subsidiária pertence à mesma empresa principal
     *
     * @param referenceCnpj CNPJ de referência (da empresa principal)
     * @param cnpjToCheck CNPJ a ser verificado (da subsidiária)
     * @return true se pertencem à mesma empresa, false caso contrário
     */
    public boolean isSameCompany(String referenceCnpj, String cnpjToCheck) {
        return CnpjUtils.isSameCompany(referenceCnpj, cnpjToCheck);
    }

    /**
     * Busca uma empresa pelo CNPJ
     *
     * @param documentNumber CNPJ da empresa
     * @return A empresa encontrada ou lança exceção se não existir
     */
    @Transactional(readOnly = true)
    public Company findByDocumentNumber(String documentNumber) {
        String formattedCnpj = CnpjUtils.formatCnpj(documentNumber);
        return repo.findByDocumentNumber(formattedCnpj)
                .orElseThrow(() -> new EntityNotFoundException("Company with CNPJ %s not found".formatted(formattedCnpj)));
    }
}