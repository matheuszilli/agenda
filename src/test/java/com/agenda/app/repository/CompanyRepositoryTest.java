package com.agenda.app.repository;

import com.agenda.app.model.Address;
import com.agenda.app.model.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Deve salvar uma empresa com sucesso")
    void testSaveCompany() {
        // Criação dos dados de teste
        Address address = new Address();
        address.setStreet("Rua do Teste");
        address.setNumber("123");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("01234-567");

        Company company = new Company();
        company.setName("Empresa de Teste");
        company.setDocumentNumber("12345678901234");
        company.setPhone("(11) 98765-4321");
        company.setAddress(address);

        // Salvando a empresa
        Company savedCompany = companyRepository.save(company);

        // Verificações
        assertNotNull(savedCompany);
        assertNotNull(savedCompany.getId());
        assertEquals("Empresa de Teste", savedCompany.getName());
        assertEquals("12345678901234", savedCompany.getDocumentNumber());
        assertEquals("Rua do Teste", savedCompany.getAddress().getStreet());
    }

    @Test
    @DisplayName("Deve buscar uma empresa por ID")
    void testFindCompanyById() {
        // Criação e salvamento da empresa
        Company company = new Company();
        company.setName("Empresa Consulta");
        company.setDocumentNumber("98765432109876");
        company.setPhone("(11) 91234-5678");

        Address address = new Address();
        address.setStreet("Av. Paulista");
        address.setNumber("1000");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("01310-100");
        company.setAddress(address);

        company = companyRepository.save(company);

        // Buscando a empresa
        Optional<Company> foundCompany = companyRepository.findById(company.getId());

        // Verificações
        assertTrue(foundCompany.isPresent());
        assertEquals("Empresa Consulta", foundCompany.get().getName());
        assertEquals("98765432109876", foundCompany.get().getDocumentNumber());
    }

    @Test
    @DisplayName("Deve verificar existência de empresa pelo nome")
    void testExistsByName() {
        Company company = new Company();
        company.setName("Empresa Única");
        company.setDocumentNumber("11223344556677");
        company.setCreatedAt(Instant.now());

        Address address = new Address();
        address.setStreet("Rua Única");
        address.setNumber("1");
        address.setCity("Rio de Janeiro");
        address.setState("RJ");
        address.setZipCode("20000-000");
        company.setAddress(address);

        companyRepository.save(company);

        // Verificações
        assertTrue(companyRepository.existsByName("Empresa Única"));
        assertFalse(companyRepository.existsByName("Empresa Inexistente"));
    }

    @Test
    @DisplayName("Deve buscar empresas pelo nome parcial")
    void testFindByNameContainingIgnoreCase() {
        // Criação e salvamento das empresas
        Company company1 = new Company();
        company1.setName("Clínica Médica Central");
        company1.setDocumentNumber("11111111111111");
        company1.setAddress(createFakeAddress());
        company1.setCreatedAt(Instant.now());
        company1.setAddress(createFakeAddress());

        Company company2 = new Company();
        company2.setName("Hospital Central");
        company2.setDocumentNumber("22222222222222");
        company2.setAddress(createFakeAddress());
        company2.setCreatedAt(Instant.now());
        company2.setAddress(createFakeAddress());

        Company company3 = new Company();
        company3.setName("Laboratório Avançado");
        company3.setDocumentNumber("33333333333333");
        company3.setAddress(createFakeAddress());
        company3.setCreatedAt(Instant.now());
        company3.setAddress(createFakeAddress());

        companyRepository.save(company1);
        companyRepository.save(company2);
        companyRepository.save(company3);

        // Buscando empresas com "Central" no nome
        List<Company> foundCompanies = companyRepository.findByNameContainingIgnoreCase("central");

        // Verificações
        assertEquals(2, foundCompanies.size());
        assertTrue(foundCompanies.stream().anyMatch(c -> c.getName().equals("Clínica Médica Central")));
        assertTrue(foundCompanies.stream().anyMatch(c -> c.getName().equals("Hospital Central")));
        assertFalse(foundCompanies.stream().anyMatch(c -> c.getName().equals("Laboratório Avançado")));
    }

    @Test
    @DisplayName("Deve excluir uma empresa com sucesso")
    void testDeleteCompany() {
        // Criação e salvamento da empresa
        Company company = new Company();
        company.setName("Empresa para Excluir");
        company.setDocumentNumber("44444444444444");
        company.setAddress(createFakeAddress());
        company.setCreatedAt(Instant.now());
        company = companyRepository.save(company);

        // Verificando se foi salva
        assertTrue(companyRepository.existsById(company.getId()));

        // Excluindo a empresa
        companyRepository.deleteById(company.getId());

        // Verificando se foi excluída
        assertFalse(companyRepository.existsById(company.getId()));
    }

    private Address createFakeAddress() {
        Address address = new Address();
        address.setStreet("Rua Teste");
        address.setNumber("123");
        address.setCity("SP");
        address.setState("SP");
        address.setZipCode("00000-000");
        return address;
    }
}