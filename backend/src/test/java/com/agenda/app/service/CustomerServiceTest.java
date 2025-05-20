package com.agenda.app.service;

import com.agenda.app.dto.AddressRequest;
import com.agenda.app.dto.CustomerRequest;
import com.agenda.app.dto.CustomerResponse;
import com.agenda.app.mapper.CustomerMapper;
import com.agenda.app.model.Address;
import com.agenda.app.model.Company;
import com.agenda.app.model.Customer;
import com.agenda.app.repository.CompanyRepository;
import com.agenda.app.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private UUID companyId;
    private UUID customerId;
    private Company company;
    private Customer customer;
    private CustomerRequest customerRequest;
    private CustomerResponse customerResponse;
    private Address address;

    @BeforeEach
    void setUp() {
        // Configurar dados de teste
        companyId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        address = new Address();
        address.setStreet("Rua do Cliente");
        address.setNumber("456");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("04567-890");

        company = new Company();
        company.setId(companyId);
        company.setName("Empresa Teste");

        customer = new Customer();
        customer.setId(customerId);
        customer.setFirstName("Cliente");
        customer.setLastName("Teste");
        customer.setFullName("Cliente Teste");
        customer.setEmail("cliente@teste.com");
        customer.setPhone("(11) 98765-4321");
        customer.setDocumentNumber("12345678900");
        customer.setAddress(address);
        customer.setCompany(company);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Rua do Cliente");
        addressRequest.setNumber("456");
        addressRequest.setCity("São Paulo");
        addressRequest.setState("SP");
        addressRequest.setZipCode("04567-890");

        customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Cliente");
        customerRequest.setLastName("Teste");
        customerRequest.setEmail("cliente@teste.com");
        customerRequest.setPhone("(11) 98765-4321");
        customerRequest.setDocumentNumber("12345678900");
        customerRequest.setAddress(addressRequest);
        customerRequest.setCompanyId(companyId);

        customerResponse = new CustomerResponse(
                customerId,
                "Cliente",
                "Teste",
                "Cliente Teste",
                "cliente@teste.com",
                "(11) 98765-4321",
                "12345678900",
                null, // O mapeamento real seria feito pelo mapper
                companyId
        );
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void testCreateCustomer() {
        // Configuração dos mocks
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(customerMapper.toEntity(eq(customerRequest), any(Company.class))).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        // Execução
        CustomerResponse result = customerService.create(companyId, customerRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals("Cliente", result.firstName());
        assertEquals("Teste", result.lastName());
        assertEquals("Cliente Teste", result.fullName());

        // Verifica chamadas aos mocks
        verify(companyRepository).findById(companyId);
        verify(customerMapper).toEntity(eq(customerRequest), any(Company.class));
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com empresa inexistente")
    void testCreateCustomerWithInvalidCompany() {
        // Configuração
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Execução e verificação
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            customerService.create(companyId, customerRequest);
        });

        assertTrue(exception.getMessage().contains("Company not found"));
        verify(companyRepository).findById(companyId);
        verify(customerMapper, never()).toEntity(any(), any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar clientes por empresa")
    void testListCustomersByCompany() {
        // Configuração
        List<Customer> customers = Arrays.asList(customer);
        List<CustomerResponse> expectedResponses = Arrays.asList(customerResponse);

        when(customerRepository.findByCompanyId(companyId)).thenReturn(customers);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        // Execução
        List<CustomerResponse> result = customerService.listByCompany(companyId);

        // Verificação
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).id());
        assertEquals("Cliente Teste", result.get(0).fullName());

        verify(customerRepository).findByCompanyId(companyId);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    @DisplayName("Deve buscar um cliente por ID")
    void testGetCustomerById() {
        // Configuração
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        // Execução
        CustomerResponse result = customerService.getById(companyId, customerId);

        // Verificação
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals("Cliente Teste", result.fullName());

        verify(customerRepository).findById(customerId);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente de outra empresa")
    void testGetCustomerByIdFromOtherCompany() {
        // Criamos um cliente de outra empresa
        UUID otherCompanyId = UUID.randomUUID();
        Company otherCompany = new Company();
        otherCompany.setId(otherCompanyId);

        Customer customerFromOtherCompany = new Customer();
        customerFromOtherCompany.setId(customerId);
        customerFromOtherCompany.setCompany(otherCompany);

        // Configuração
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerFromOtherCompany));

        // Execução e verificação
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            customerService.getById(companyId, customerId);
        });

        assertTrue(exception.getMessage().contains("not found in this company"));
        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void testUpdateCustomer() {
        // Configuração
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerMapper).updateFromRequest(customerRequest, customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        // Execução
        CustomerResponse result = customerService.update(companyId, customerId, customerRequest);

        // Verificação
        assertNotNull(result);
        assertEquals(customerId, result.id());

        verify(customerRepository).findById(customerId);
        verify(customerMapper).updateFromRequest(customerRequest, customer);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    @DisplayName("Deve excluir um cliente com sucesso")
    void testDeleteCustomer() {
        // Configuração
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        // Execução
        customerService.delete(companyId, customerId);

        // Verificação
        verify(customerRepository).findById(customerId);
        verify(customerRepository).delete(customer);
    }
}