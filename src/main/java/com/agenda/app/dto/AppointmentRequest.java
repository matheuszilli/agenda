package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull UUID customerId,
        @NotNull UUID professionalId,
        @NotNull UUID serviceId,
        @NotNull UUID subsidiaryId,
        @NotNull UUID companyId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        String notes,
        UUID paymentId
) {}
