package com.agenda.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfessionalServiceCfgRequest(
        UUID professionalId,
        UUID serviceId,
        BigDecimal customPrice,
        Integer customDurationMinutes,
        BigDecimal commissionPct,
        BigDecimal commissionFixed
) {}