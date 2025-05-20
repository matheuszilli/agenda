package com.agenda.app.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para criar horários recorrentes
 */
@Data
public class RecurringScheduleRequest {

    /**
     * Horário de abertura (ou início do trabalho para profissionais)
     */
    @NotNull
    private LocalTime openTime;

    /**
     * Horário de fechamento (ou término do trabalho para profissionais)
     */
    @NotNull
    private LocalTime closeTime;

    /**
     * Lista de dias da semana (1-7, onde 1=Segunda, 7=Domingo)
     */
    @NotEmpty
    private List<Integer> daysOfWeek;

    /**
     * Data de início do padrão recorrente
     */
    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    /**
     * Data de término do padrão recorrente
     */
    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

    /**
     * Se deve substituir horários existentes
     */
    private boolean replaceExisting = false;
}