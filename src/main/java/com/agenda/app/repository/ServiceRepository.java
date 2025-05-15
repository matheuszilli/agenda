package com.agenda.app.repository;

import com.agenda.app.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Item, UUID> {

}
