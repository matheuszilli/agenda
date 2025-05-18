package com.agenda.app.service;

import com.agenda.app.dto.CompanyRequest;
import com.agenda.app.dto.CompanyResponse;
import com.agenda.app.mapper.CompanyMapper;
import com.agenda.app.model.Address;
import com.agenda.app.model.Company;
import com.agenda.app.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    private UUID companyId;
    private Company company;
    private CompanyRequest companyRequest;
    private CompanyResponse companyResponse;
    private Address address;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();

        address = new Address();
        address.setStreet("Rua Teste");
        address.setNumber("123");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("01234-567");

        company = new Company();
        company.setId(companyId);
        company.setName("EMPRESA MOLHO DO MOLHO");
        company.setDocumentNumber("12345678901234");
        company.setPhone("(11) 99999-9999");
        company.setAddress(address);

        companyRequest = new CompanyRequest();
        companyRequest.setName("EMPRESA MOLHO DO MOLHO");
        companyRequest.setDocumentNumber("12345678901234");
        companyRequest.setPhone("(11) 99999-9999");

        companyResponse = new CompanyResponse(
                companyId,
                "EMPRESA MOLHO DO MOLHO",
                null,
                "(11) 99999-9999",
                "12345678901234"
        );
    }

    @Test
    @DisplayName("Deve criar uma empresa com sucesso")
    void testCreateCompany() {
        // Configuração dos mocks
        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyMapper.toEntity(any(CompanyRequest.class))).thenReturn(company);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(companyMapper.toResponse(any(Company.class))).thenReturn(companyResponse);

        // Execução do método
        CompanyResponse result = companyService.create(companyRequest);

        // Verificações
        assertNotNull(result);
        assertEquals(companyId, result.id());
        assertEquals("EMPRESA MOLHO DO MOLHO", result.name());
        assertEquals("12345678901234", result.documentNumber());

        // Verifica se os métodos foram chamados
        verify(companyRepository).existsByName("EMPRESA MOLHO DO MOLHO");
        verify(companyMapper).toEntity(companyRequest);
        verify(companyRepository).save(company);
        verify(companyMapper).toResponse(company);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar empresa com nome duplicado")
    void testCreateCompanyWithDuplicateName() {
        // Configuração dos mocks
        when(companyRepository.existsByName(anyString())).thenReturn(true);

        // Execução e verificação
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            companyService.create(companyRequest);
        });

        assertEquals("Company with name EMPRESA MOLHO DO MOLHO already exists", exception.getMessage());
        verify(companyRepository).existsByName("EMPRESA MOLHO DO MOLHO");
        verify(companyMapper, never()).toEntity(any());
        verify(companyRepository, never()).save(any());
        System.out.println(exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar uma empresa por ID com sucesso")
    void testGetCompanyById() {
        // Configuração dos mocks
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMapper.toResponse(company)).thenReturn(companyResponse);

        // Execução
        CompanyResponse result = companyService.get(companyId);

        // Verificações
        assertNotNull(result);
        assertEquals(companyId, result.id());
        assertEquals("EMPRESA MOLHO DO MOLHO", result.name());

        verify(companyRepository).findById(companyId);
        verify(companyMapper).toResponse(company);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar empresa com ID inexistente")
    void testGetCompanyByIdNotFound() {
        // Configuração dos mocks
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // Execução e verificação
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            companyService.get(companyId);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(companyRepository).findById(companyId);
        verify(companyMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve listar todas as empresas com sucesso")
    void testGetAllCompanies() {
        // Configuração
        List<Company> companies = Arrays.asList(company);
        List<CompanyResponse> expectedResponses = Arrays.asList(companyResponse);

        when(companyRepository.findAll()).thenReturn(companies);
        when(companyMapper.toResponseList(companies)).thenReturn(expectedResponses);

        // Execução
        List<CompanyResponse> result = companyService.getAll(null);

        // Verificação
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(companyId, result.get(0).id());

        verify(companyRepository).findAll();
        verify(companyMapper).toResponseList(companies);
    }

    @Test
    @DisplayName("Deve atualizar uma empresa com sucesso")
    void testUpdateCompany() {
        // Configuração
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        doNothing().when(companyMapper).updateEntityFromDto(any(), any());
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toResponse(company)).thenReturn(companyResponse);

        // Execução
        CompanyResponse result = companyService.update(companyId, companyRequest);

        // Verificação
        assertNotNull(result);
        assertEquals(companyId, result.id());

        verify(companyRepository).findById(companyId);
        verify(companyMapper).updateEntityFromDto(companyRequest, company);
        verify(companyRepository).save(company);
        verify(companyMapper).toResponse(company);
    }

    @Test
    @DisplayName("Deve excluir uma empresa com sucesso")
    void testDeleteCompany() {
        // Configuração
        when(companyRepository.existsById(companyId)).thenReturn(true);
        doNothing().when(companyRepository).deleteById(companyId);

        // Execução
        companyService.delete(companyId);

        // Verificação
        verify(companyRepository).existsById(companyId);
        verify(companyRepository).deleteById(companyId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir empresa inexistente")
    void testDeleteCompanyNotFound() {
        // Configuração
        when(companyRepository.existsById(companyId)).thenReturn(false);

        // Execução e verificação
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            companyService.delete(companyId);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(companyRepository).existsById(companyId);
        verify(companyRepository, never()).deleteById(any());
    }
}