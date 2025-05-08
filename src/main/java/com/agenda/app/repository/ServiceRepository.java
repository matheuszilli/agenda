package com.agenda.app.repository;

import com.agenda.app.model.BusinessService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<BusinessService, UUID> {

}
