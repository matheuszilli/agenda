package com.agenda.app.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO para resposta de atribuições de profissionais a cadeiras/salas
 */
@Data
public class ProfessionalChairRoomAssignmentResponse {
    
    private UUID id;
    private UUID professionalId;
    private String professionalName;
    private UUID chairRoomId;
    private String chairRoomName;
    private LocalDate date;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean recurring;
}