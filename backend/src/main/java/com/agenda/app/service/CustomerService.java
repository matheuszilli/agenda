package com.agenda.app.service;

import com.agenda.app.dto.CustomerRequest;
import com.agenda.app.dto.CustomerResponse;
import com.agenda.app.mapper.CustomerMapper;
import com.agenda.app.model.Company;
import com.agenda.app.model.Customer;
import com.agenda.app.repository.CompanyRepository;
import com.agenda.app.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repo;
    private final CompanyRepository companyRepo;
    private final CustomerMapper mapper;

    @Transactional
    public CustomerResponse create(UUID companyId, CustomerRequest req) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found: " + companyId));

        Customer customer = mapper.toEntity(req, company);
        Customer saved = repo.save(customer);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> listByCompany(UUID companyId) {
        return repo.findByCompanyId(companyId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID companyId, UUID customerId) {
        Customer cust = repo.findById(customerId)
                .filter(c -> c.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found in this company"));
        return mapper.toResponse(cust);
    }

    @Transactional
    public CustomerResponse update(UUID companyId, UUID customerId, CustomerRequest req) {
        Customer cust = repo.findById(customerId)
                .filter(c -> c.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found in this company"));

        mapper.updateFromRequest(req, cust);
        Customer updated = repo.save(cust);
        return mapper.toResponse(updated);
    }

    @Transactional
    public void delete(UUID companyId, UUID customerId) {
        Customer cust = repo.findById(customerId)
                .filter(c -> c.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new EntityNotFoundException("Customer not found in this company"));
        repo.delete(cust);
    }
}