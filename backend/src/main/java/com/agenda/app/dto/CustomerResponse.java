package com.agenda.app.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String documentNumber,
        AddressResponse address,
        UUID companyId
) {}
