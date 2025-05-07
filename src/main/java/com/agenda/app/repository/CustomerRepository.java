package com.agenda.app.repository;

import com.agenda.app.model.Customer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    /**
     * Busca um cliente pelo seu Documento (CPF ou CNPJ ou Passaporte).
     *
     * @return Cliente encontrado ou null se não encontrado.
     */
    List<Customer> findByCustomerDocument(
            String document
    );

    /**
     * Busca um cliente pelo seu e-mail.
     *
     * @param email E-mail do cliente.
     * @return Cliente encontrado ou null se não encontrado.
     */
    Customer findByEmail(String email);
}
