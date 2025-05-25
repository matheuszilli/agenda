package com.agenda.app.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para busca de slots disponíveis para agendamento
 */
@Data
public class AvailabilityRequest {
    
    @NotNull(message = "Subsidiary ID is required")
    private UUID subsidiaryId;
    
    @NotNull(message = "Item ID is required")
    private UUID itemId;
    
    @NotNull(message = "Professional ID is required")
    private UUID professionalId;
    
    // ChairRoom é opcional
    private UUID chairRoomId;
    
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
    
    @NotNull(message = "Duration in minutes is required")
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    private Integer durationMinutes;
}