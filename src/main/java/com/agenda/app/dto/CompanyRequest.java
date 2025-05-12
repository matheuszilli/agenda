package com.agenda.app.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CompanyRequest(
        @NotBlank String name,
        @NotNull AddressRequest address,
        String phone,
        @NotBlank String documentNumber
) {}
