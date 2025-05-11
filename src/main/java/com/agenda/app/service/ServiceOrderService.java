package com.agenda.app.service;

import lombok.RequiredArgsConstructor;
import com.agenda.app.model.BusinessService;
import com.agenda.app.model.ServiceOrder;
import com.agenda.app.model.ServiceOrderStatus;
import com.agenda.app.repository.ServiceOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;

    public ServiceOrder createServiceOrder(ServiceOrder serviceOrder){
        require(serviceOrder.getBusinessServices(), "Service is required");
        require(serviceOrder.getCustomer(), "Customer is required");
        require(serviceOrder.getProfessional(), "Professional is required");
        require(serviceOrder.getStatus(), "Status is required");


        // Calcula o total somando os preços dos serviços
        BigDecimal total = serviceOrder.getBusinessServices().stream()
                .map(BusinessService::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplica o desconto, se houver
        BigDecimal discount = serviceOrder.getDiscount() != null ? serviceOrder.getDiscount() : BigDecimal.ZERO;
        serviceOrder.setTotalPrice(total.subtract(discount));



        boolean alreadyExists = serviceOrderRepository.existsByCustomerAndBusinessServicesInAndProfessional(
                serviceOrder.getCustomer(), serviceOrder.getBusinessServices(), serviceOrder.getProfessional()
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
