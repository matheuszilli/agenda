package com.agenda.app.service;

import com.agenda.app.dto.ItemResponse;
import com.agenda.app.dto.SubsidiaryRequest;
import com.agenda.app.dto.SubsidiaryResponse;
import com.agenda.app.mapper.SubsidiaryMapper;
import com.agenda.app.mapper.ItemMapper;
import com.agenda.app.model.Company;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.repository.CompanyRepository;
import com.agenda.app.repository.ServiceRepository;
import com.agenda.app.repository.SubsidiaryRepository;
import com.agenda.app.util.CnpjUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubsidiaryService {

    private final SubsidiaryRepository subsidiaryRepository;
    private final CompanyRepository companyRepository;
    private final SubsidiaryMapper mapper;
    private final ServiceRepository serviceRepository;
    private final ItemMapper itemMapper;

    /* ======================= CREATE ======================= */
    @Transactional
    public SubsidiaryResponse create(SubsidiaryRequest dto) {
        // Verificar se já existe subsidiária com mesmo nome na mesma empresa
        if (subsidiaryRepository.existsByNameIgnoreCaseAndCompanyId(dto.getName(), dto.getCompanyId())) {
            throw new IllegalArgumentException("Subsidiary already exists");
        }

        // Buscar empresa relacionada
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        // Formatando o CNPJ da subsidiária
        String formattedSubsidiaryCnpj;
        try {
            formattedSubsidiaryCnpj = CnpjUtils.formatCnpj(dto.getDocumentNumber());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CNPJ format: " + e.getMessage());
        }

        // Validar que o CNPJ da subsidiária tem a mesma raiz que a empresa
        if (!CnpjUtils.isSameCompany(company.getDocumentNumber(), formattedSubsidiaryCnpj)) {
            throw new IllegalArgumentException(
                    "Subsidiary CNPJ must have the same root as its parent company. " +
                            "Expected root: " + CnpjUtils.extractCnpjRoot(company.getDocumentNumber()));
        }

        // Criar objeto de requisição com CNPJ formatado
        SubsidiaryRequest formattedRequest = new SubsidiaryRequest(
                dto.getName(),
                dto.getAddress(),
                formattedSubsidiaryCnpj,
                dto.getCompanyId()
        );

        Subsidiary entity = mapper.toEntity(formattedRequest, company);
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

    @Transactional(readOnly = true)
    public List<SubsidiaryResponse> listAll() {
        return subsidiaryRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SubsidiaryResponse> listByCompany(UUID companyId) {
        return subsidiaryRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listServicesBySubsidiary(UUID subsidiaryId) {
        return serviceRepository.findBySubsidiaryId(subsidiaryId)
                .stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /* ======================= UPDATE ======================= */
    @Transactional
    public SubsidiaryResponse update(UUID id, SubsidiaryRequest dto) {
        Subsidiary entity = subsidiaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));

        // Formatando o CNPJ da subsidiária
        String formattedSubsidiaryCnpj;
        try {
            formattedSubsidiaryCnpj = CnpjUtils.formatCnpj(dto.getDocumentNumber());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid CNPJ format: " + e.getMessage());
        }

        // Validar que o CNPJ da subsidiária tem a mesma raiz que a empresa
        if (!CnpjUtils.isSameCompany(entity.getCompany().getDocumentNumber(), formattedSubsidiaryCnpj)) {
            throw new IllegalArgumentException(
                    "Subsidiary CNPJ must have the same root as its parent company. " +
                            "Expected root: " + CnpjUtils.extractCnpjRoot(entity.getCompany().getDocumentNumber()));
        }

        // Criar objeto de requisição com CNPJ formatado
        SubsidiaryRequest formattedRequest = new SubsidiaryRequest(
                dto.getName(),
                dto.getAddress(),
                formattedSubsidiaryCnpj,
                dto.getCompanyId()
        );

        // Se quiser impedir troca de empresa, não mexa em company aqui
        mapper.copyNonNullToEntity(formattedRequest, entity);
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