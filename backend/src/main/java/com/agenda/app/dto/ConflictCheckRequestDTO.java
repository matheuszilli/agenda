package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO para solicitação de verificação de conflitos
 */
@Data
public class ConflictCheckRequestDTO {
    
    @NotNull(message = "Chair room ID is required")
    private UUID chairRoomId;
    
    private List<LocalDate> dates;
    
    private List<Integer> daysOfWeek;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private boolean includeCustomized = true;
}