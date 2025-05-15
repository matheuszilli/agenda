package com.agenda.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponse(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Integer durationMinutes,
    Boolean requiresPrePayment,
    UUID companyId,
    Boolean active
    ) {}
