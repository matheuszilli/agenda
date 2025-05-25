package com.agenda.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO para resposta de verificação de conflitos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConflictCheckResponseDTO {
    
    private UUID chairRoomId;
    private boolean hasConflicts = false;
    private List<LocalDate> conflictingDates = new ArrayList<>();
    
    /**
     * Construtor conveniente para criar resposta sem conflitos
     */
    public ConflictCheckResponseDTO(UUID chairRoomId) {
        this.chairRoomId = chairRoomId;
    }
    
    /**
     * Adiciona uma data de conflito e marca que há conflitos
     */
    public void addConflict(LocalDate date) {
        this.hasConflicts = true;
        this.conflictingDates.add(date);
    }
}