package com.agenda.app.dto;

import com.agenda.app.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/** O que o frontend realmente consome depois de criar/buscar */
public record AppointmentResponse(
        UUID id,
        String professionalId,
        String customerId,
        String subsidiaryId,
        String serviceId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status
) {}