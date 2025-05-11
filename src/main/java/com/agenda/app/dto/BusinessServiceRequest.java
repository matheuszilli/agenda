// src/main/java/com/agenda/app/dto/BusinessServiceRequest.java
package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record BusinessServiceRequest(
        @NotBlank String name,
        String description,
        @Positive BigDecimal price,
        @Positive Integer durationMinutes,
        @NotNull Boolean requiresPrePayment,
        @NotNull UUID companyId, //Empresa dona do serviço
        Boolean active
) {}
