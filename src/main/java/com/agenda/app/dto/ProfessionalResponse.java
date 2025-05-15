package com.agenda.app.dto;

import com.agenda.app.model.Address;

import java.time.LocalTime;
import java.util.UUID;

public record ProfessionalResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String documentNumber,
        Address address,
        String phone,
        String email,
        UUID subsidiaryId
) {}