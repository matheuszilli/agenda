package com.agenda.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para configuração de horários recorrentes
 */
@Data
public class RecurringScheduleDTO {

    @NotNull(message = "Chair room ID is required")
    private UUID chairRoomId;

    @NotEmpty(message = "At least one day configuration is required")
    private Map<Integer, @Valid DayScheduleConfigDTO> weekSchedule;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    private boolean replaceExisting = false;
}