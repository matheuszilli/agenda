package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter
public class ChairRoomScheduleEntryResponse {
    @NotNull private UUID id;
    @NotNull private UUID chairRoomId;
    @NotNull private LocalDate date;
    @NotNull private LocalTime openTime;
    @NotNull private LocalTime closeTime;
    @NotNull private boolean closed;
    private boolean customized;
}