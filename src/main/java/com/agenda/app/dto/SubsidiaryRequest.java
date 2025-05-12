package com.agenda.app.dto;

import com.agenda.app.model.Address;
import com.agenda.app.model.SubsidiaryDaysOpen;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record SubsidiaryRequest(
        @NotBlank String name,
        @NotNull  Address address,
        @NotNull  LocalTime openTime,
        @NotNull  LocalTime closeTime,
        @NotEmpty Set<SubsidiaryDaysOpen> daysOpen,
        @NotBlank String documentNumber,
        @NotNull  UUID companyId
) {}