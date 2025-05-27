package com.agenda.app.repository;

import com.agenda.app.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    boolean existsByNameIgnoreCaseAndCompanyId(
            String name,
            UUID companyId
    );

    List<Item> findBySubsidiaryId(UUID subsidiaryId);
    
    List<Item> findByCompanyId(UUID companyId);

}
