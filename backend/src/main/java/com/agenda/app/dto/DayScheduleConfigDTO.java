package com.agenda.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.agenda.app.validation.TimeRange;

import java.time.LocalTime;

/**
 * DTO para configuração de horário por dia da semana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TimeRange(startTimeField = "openTime", endTimeField = "closeTime", 
        message = "Closing time must be after opening time")
public class DayScheduleConfigDTO {
    private boolean open = true;
    
    @NotNull(message = "Opening time is required")
    private LocalTime openTime;
    
    @NotNull(message = "Closing time is required")
    private LocalTime closeTime;
}