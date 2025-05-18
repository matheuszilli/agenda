package com.agenda.app.controller;

import com.agenda.app.dto.AddressRequest;
import com.agenda.app.dto.CompanyRequest;
import com.agenda.app.dto.CompanyResponse;
import com.agenda.app.model.Address;
import com.agenda.app.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar uma empresa com sucesso")
    void testCreateCompany() throws Exception {
        // Criação de dados de teste
        UUID companyId = UUID.randomUUID();

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Rua Teste");
        addressRequest.setNumber("123");
        addressRequest.setCity("São Paulo");
        addressRequest.setState("SP");
        addressRequest.setZipCode("01234-567");

        CompanyRequest request = new CompanyRequest();
        request.setName("Empresa Teste");
        request.setDocumentNumber("12345678901234");
        request.setPhone("(11) 99999-9999");
        request.setAddress(addressRequest);

        Address address = new Address();
        address.setStreet("Rua Teste");
        address.setNumber("123");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("01234-567");

        CompanyResponse response = new CompanyResponse(
                companyId,
                "Empresa Teste",
                null, // O mapper real converteria o Address para AddressResponse
                "(11) 99999-9999",
                "12345678901234"
        );

        // Configuração do mock
        when(companyService.create(any(CompanyRequest.class))).thenReturn(response);

        // Execução da requisição
        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(companyId.toString())))
                .andExpect(jsonPath("$.name", is("Empresa Teste")))
                .andExpect(jsonPath("$.documentNumber", is("12345678901234")));

        // Verificação da chamada ao serviço
        verify(companyService).create(any(CompanyRequest.class));
    }

    @Test
    @DisplayName("Deve obter uma empresa por ID")
    void testGetCompanyById() throws Exception {
        // Criação de dados de teste
        UUID companyId = UUID.randomUUID();

        CompanyResponse response = new CompanyResponse(
                companyId,
                "Empresa Teste",
                null,
                "(11) 99999-9999",
                "12345678901234"
        );

        // Configuração do mock
        when(companyService.get(companyId)).thenReturn(response);

        // Execução da requisição
        mockMvc.perform(get("/companies/" + companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(companyId.toString())))
                .andExpect(jsonPath("$.name", is("Empresa Teste")));

        // Verificação
        verify(companyService).get(companyId);
    }

    @Test
    @DisplayName("Deve listar todas as empresas")
    void testGetAllCompanies() throws Exception {
        // Criação de dados de teste
        UUID companyId1 = UUID.randomUUID();
        UUID companyId2 = UUID.randomUUID();

        List<CompanyResponse> companies = Arrays.asList(
                new CompanyResponse(companyId1, "Empresa 1", null, "(11) 1111-1111", "11111111111111"),
                new CompanyResponse(companyId2, "Empresa 2", null, "(11) 2222-2222", "22222222222222")
        );

        // Configuração do mock
        when(companyService.getAll(null)).thenReturn(companies);

        // Execução da requisição
        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(companyId1.toString())))
                .andExpect(jsonPath("$[0].name", is("Empresa 1")))
                .andExpect(jsonPath("$[1].id", is(companyId2.toString())))
                .andExpect(jsonPath("$[1].name", is("Empresa 2")));

        // Verificação
        verify(companyService).getAll(null);
    }

    @Test
    @DisplayName("Deve pesquisar empresas por nome")
    void testSearchCompaniesByName() throws Exception {
        // Criação de dados de teste
        UUID companyId = UUID.randomUUID();
        List<CompanyResponse> companies = Arrays.asList(
                new CompanyResponse(companyId, "Empresa Exemplo", null, "(11) 1111-1111", "11111111111111")
        );

        // Configuração do mock
        when(companyService.getAll("Exemplo")).thenReturn(companies);

        // Execução da requisição
        mockMvc.perform(get("/companies?name=Exemplo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(companyId.toString())))
                .andExpect(jsonPath("$[0].name", is("Empresa Exemplo")));

        // Verificação
        verify(companyService).getAll("Exemplo");
    }

    @Test
    @DisplayName("Deve atualizar uma empresa com sucesso")
    void testUpdateCompany() throws Exception {
        // Criação de dados de teste
        UUID companyId = UUID.randomUUID();

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Rua Atualizada");
        addressRequest.setNumber("456");
        addressRequest.setCity("Rio de Janeiro");
        addressRequest.setState("RJ");
        addressRequest.setZipCode("20000-000");

        CompanyRequest request = new CompanyRequest();
        request.setName("Empresa Atualizada");
        request.setDocumentNumber("98765432109876");
        request.setPhone("(21) 98765-4321");
        request.setAddress(addressRequest);

        CompanyResponse response = new CompanyResponse(
                companyId,
                "Empresa Atualizada",
                null,
                "(21) 98765-4321",
                "98765432109876"
        );

        // Configuração do mock
        when(companyService.update(eq(companyId), any(CompanyRequest.class))).thenReturn(response);

        // Execução da requisição
        mockMvc.perform(put("/companies/" + companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(companyId.toString())))
                .andExpect(jsonPath("$.name", is("Empresa Atualizada")))
                .andExpect(jsonPath("$.documentNumber", is("98765432109876")));

        // Verificação
        verify(companyService).update(eq(companyId), any(CompanyRequest.class));
    }

    @Test
    @DisplayName("Deve excluir uma empresa com sucesso")
    void testDeleteCompany() throws Exception {
        // Criação de dados de teste
        UUID companyId = UUID.randomUUID();

        // Configuração do mock
        doNothing().when(companyService).delete(companyId);

        // Execução da requisição
        mockMvc.perform(delete("/companies/" + companyId))
                .andExpect(status().isNoContent());

        // Verificação
        verify(companyService).delete(companyId);
    }
}