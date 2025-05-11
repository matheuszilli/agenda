package com.agenda.app.repository;

import com.agenda.app.model.BusinessService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface BusinessServiceRepository extends JpaRepository<BusinessService, UUID> {

    boolean existsByNameIgnoreCaseAndCompanyId(
            String name,
            UUID companyId
    );
}
