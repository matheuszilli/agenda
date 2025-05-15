package com.agenda.app.service;

import com.agenda.app.model.Item;
import lombok.RequiredArgsConstructor;
import com.agenda.app.model.ServiceOrder;
import com.agenda.app.model.ServiceOrderStatus;
import com.agenda.app.repository.ServiceOrderRepository;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;

    public ServiceOrder createServiceOrder(ServiceOrder serviceOrder){
        require(serviceOrder.getItems(), "Service is required");
        require(serviceOrder.getCustomer(), "Customer is required");
        require(serviceOrder.getProfessional(), "Professional is required");
        require(serviceOrder.getStatus(), "Status is required");


        // Calcula o total somando os preços dos serviços
        BigDecimal total = serviceOrder.getItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplica o desconto, se houver
        BigDecimal discount = serviceOrder.getDiscount() != null ? serviceOrder.getDiscount() : BigDecimal.ZERO;
        serviceOrder.setTotalPrice(total.subtract(discount));



        boolean alreadyExists = serviceOrderRepository.existsByCustomerAndItemsInAndProfessional(
                serviceOrder.getCustomer(), serviceOrder.getItems(), serviceOrder.getProfessional()
        );

        if(alreadyExists){
            throw new IllegalArgumentException("Service order already exists");
        }

        // Define status inicial
        serviceOrder.setStatus(ServiceOrderStatus.OPEN);

        return serviceOrderRepository.save(serviceOrder);
    }

    private void require(Object value, String message){
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }


}
