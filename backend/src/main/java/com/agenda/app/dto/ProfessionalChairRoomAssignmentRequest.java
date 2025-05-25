package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para criação de atribuições de profissionais a cadeiras/salas
 */
@Data
public class ProfessionalChairRoomAssignmentRequest {
    
    @NotNull(message = "Professional ID is required")
    private UUID professionalId;
    
    @NotNull(message = "Chair/Room ID is required")
    private UUID chairRoomId;
    
    // Para atribuições em datas específicas
    private LocalDate date;
    
    // Para atribuições recorrentes
    @Min(value = 1, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    @Max(value = 7, message = "Day of week must be between 1 (Monday) and 7 (Sunday)")
    private Integer dayOfWeek;
    
    // Tipo de atribuição: "single" (única) ou "recurring" (recorrente)
    @NotNull(message = "Assignment type is required")
    private String assignmentType;
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    // Para atribuições em um intervalo de datas
    private List<Integer> daysOfWeek;
    private LocalDate startDate;
    private LocalDate endDate;
}