package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public record CompanyRequest(
    @NotBlank String name,
    @NotBlank String tradingName,
    @NotNull AddressRequest address,
    String phone,
    @NotBlank String documentNumber,
    @NotBlank String typeOfDocument
) {}
