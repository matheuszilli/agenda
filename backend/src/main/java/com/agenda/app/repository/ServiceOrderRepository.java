package com.agenda.app.repository;

import com.agenda.app.model.Customer;
import com.agenda.app.model.Item;
import com.agenda.app.model.Professional;
import com.agenda.app.model.ServiceOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, UUID> {
    boolean existsByCustomerAndItemsInAndProfessional(
            Customer customer, Collection<Item> items, Professional professional
    );
}
