package com.agenda.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO para representar um slot disponível para agendamento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID subsidiaryId;
    private UUID professionalId;
    private UUID chairRoomId; // Pode ser null se não for necessário
    private UUID itemId;
    
    // Informações adicionais para exibição
    private String professionalName;
    private String chairRoomName;
    private String itemName;
    private Integer durationMinutes;
}