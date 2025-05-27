package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemRequest(
        @NotBlank String name,
        String description,
        @Positive BigDecimal price,
        @Positive Integer durationMinutes,
        @NotNull Boolean requiresPrePayment,
        @NotNull UUID companyId, //Empresa dona do serviço
        @NotNull UUID subsidiaryId, //Subsidiária onde o serviço é oferecido
        Boolean active
) {}
