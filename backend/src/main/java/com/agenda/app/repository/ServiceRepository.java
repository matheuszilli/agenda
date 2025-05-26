package com.agenda.app.repository;

import com.agenda.app.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Item, UUID> {

    List<Item> findBySubsidiaryId(UUID subsidiaryId);

}
