package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ChairRoomScheduleEntryRequest {

    @NotNull private UUID chairRoomId;
    @NotNull private LocalDate date;
    @NotNull private LocalTime openTime;
    @NotNull private LocalTime closeTime;
    private boolean closed;
}