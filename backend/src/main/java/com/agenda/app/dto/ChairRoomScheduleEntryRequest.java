package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ChairRoomScheduleEntryRequest {

    @NotNull private UUID chairRoomId;
    private LocalDate date;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean closed;
}