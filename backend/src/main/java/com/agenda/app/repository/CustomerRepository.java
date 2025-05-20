package com.agenda.app.repository;

import com.agenda.app.model.Customer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    List<Customer> findByCompanyId(UUID companyId);
    Page<Customer> findByCompanyId(UUID companyId, Pageable pageable);
}
