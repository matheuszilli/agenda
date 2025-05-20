package com.agenda.app.dto;

import lombok.Data;

import java.util.UUID;


public record CompanyResponse(
    UUID id,
    String name,
    AddressResponse address,   // << era String
    String phone,
    String documentNumber
    ) {}
