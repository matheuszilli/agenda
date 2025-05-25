// src/main/java/com/agenda/app/repository/SubsidiaryRepository.java
package com.agenda.app.repository;

import com.agenda.app.model.Subsidiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubsidiaryRepository extends JpaRepository<Subsidiary, UUID> {

    boolean existsByNameIgnoreCaseAndCompanyId(String name, UUID companyId);
    
    List<Subsidiary> findByCompanyId(UUID companyId);

}
