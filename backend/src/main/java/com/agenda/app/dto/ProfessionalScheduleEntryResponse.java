package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter
public class ProfessionalScheduleEntryResponse {

    @NotNull private UUID id;
    @NotNull private UUID professionalId;
    @NotNull private LocalDate date;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
}