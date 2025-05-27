package com.agenda.app.dto;

import java.util.UUID;

public record ProfessionalResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String documentNumber,
        AddressDTO address,
        String phone,
        String email,
        UUID subsidiaryId
) {}