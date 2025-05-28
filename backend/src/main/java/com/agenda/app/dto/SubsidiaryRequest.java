package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubsidiaryRequest {
    @NotBlank
    private String name;
    
    @NotNull
    private AddressRequest address;
    
    @NotBlank
    private String documentNumber;
    
    @NotNull
    private UUID companyId;
    
    // Campos opcionais para horários
    private LocalTime openTime;
    private LocalTime closeTime;
    
    // Construtor com os campos obrigatórios (manter compatibilidade com código existente)
    public SubsidiaryRequest(String name, AddressRequest address, String documentNumber, UUID companyId) {
        this.name = name;
        this.address = address;
        this.documentNumber = documentNumber;
        this.companyId = companyId;
    }
}