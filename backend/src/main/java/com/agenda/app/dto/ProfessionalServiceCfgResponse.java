package com.agenda.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfessionalServiceCfgResponse(
        UUID id,
        UUID professionalId,
        UUID serviceId,
        BigDecimal priceEffective,      // calculado
        Integer durationEffective,
        BigDecimal commissionPct,
        BigDecimal commissionFixed
) {}
