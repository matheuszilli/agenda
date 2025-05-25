package com.agenda.app.dto;

import com.agenda.app.validation.TimeRange;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO para exceções de horário (dias específicos com horários diferentes)
 */
@Data
@TimeRange(startTimeField = "openTime", endTimeField = "closeTime", 
        message = "Closing time must be after opening time")
public class ExceptionScheduleDTO {
    
    @NotNull(message = "Chair room ID is required")
    private UUID chairRoomId;
    
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be today or in the future")
    private LocalDate date;
    
    @NotNull(message = "Opening time is required")
    private LocalTime openTime;
    
    @NotNull(message = "Closing time is required")
    private LocalTime closeTime;
    
    private boolean closed = false;
    
    private boolean replaceExisting = true;
}