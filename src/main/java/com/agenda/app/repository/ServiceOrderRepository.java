package com.agenda.app.repository;

import com.agenda.app.model.Customer;
import com.agenda.app.model.Professional;
import com.agenda.app.model.BusinessService;
import com.agenda.app.model.ServiceOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, UUID> {
    boolean existsByCustomerAndBusinessServicesInAndProfessional(
            Customer customer,
            List<BusinessService> businessServices,
            Professional professional
    );
}
