package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    @NotBlank String street,
    @NotBlank String number,
    @NotBlank String complement,
    @NotBlank String neighborhood,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipCode
) {}