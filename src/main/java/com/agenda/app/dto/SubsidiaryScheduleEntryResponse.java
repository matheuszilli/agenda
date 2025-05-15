package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter
public class SubsidiaryScheduleEntryResponse {
        @NotNull private UUID id;
        @NotNull private UUID subsidiaryId;
        @NotNull private LocalDate date;
        @NotNull private LocalTime openTime;
        @NotNull private LocalTime closeTime;
        @NotNull private boolean closed;
}