package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

/** Dados mínimos que o frontend precisa enviar para criar um agendamento */
public record AppointmentRequest(

        @NotNull UUID professionalId,
        @NotNull UUID customerId,
        @NotNull UUID subsidiaryId,
        @NotNull UUID serviceId,

        /** ISO-8601: "2025-05-10T14:30"  */
        @Future @NotNull LocalDateTime startTime,

        /** Opcional: se cliente já pagou, manda o ID aqui */
        UUID paymentId
) {}