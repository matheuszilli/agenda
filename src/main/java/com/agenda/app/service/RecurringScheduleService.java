package com.agenda.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serviço utilitário para criação de horários recorrentes
 * Pode ser usado para criar padrões de horários para subsidiárias, salas e profissionais
 */
@Service
@RequiredArgsConstructor
public class RecurringScheduleService {

    private final SubsidiaryScheduleEntryService subsidiaryScheduleService;
    private final ChairRoomScheduleService chairRoomScheduleService;
    private final ProfessionalScheduleService professionalScheduleService;

    /**
     * Cria horários recorrentes para uma subsidiária
     *
     * @param subsidiaryId     ID da subsidiária
     * @param openTime         Horário de abertura
     * @param closeTime        Horário de fechamento
     * @param daysOfWeek       Dias da semana (1-7, onde 1=Segunda)
     * @param startDate        Data de início do padrão
     * @param endDate          Data de término do padrão
     * @param replaceExisting  Se deve substituir horários existentes
     * @return Número de horários criados
     */
    @Transactional
    public int createRecurringSubsidiarySchedule(
            UUID subsidiaryId,
            LocalTime openTime,
            LocalTime closeTime,
            List<Integer> daysOfWeek,
            LocalDate startDate,
            LocalDate endDate,
            boolean replaceExisting) {

        // Converter números (1-7) para DayOfWeek
        List<DayOfWeek> days = daysOfWeek.stream()
                .map(day -> DayOfWeek.of(day))
                .toList();

        // Lista para armazenar todas as datas geradas com base na recorrência
        List<LocalDate> allDates = new ArrayList<>();

        // Para cada dia da semana, gerar todas as datas entre startDate e endDate
        for (DayOfWeek dayOfWeek : days) {
            // Encontrar a primeira ocorrência deste dia da semana a partir da data de início
            LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));

            // Adicionar todas as ocorrências até a data final
            while (!date.isAfter(endDate)) {
                allDates.add(date);
                date = date.plusWeeks(1); // Próxima semana
            }
        }

        // Criar ou atualizar horários para cada data
        int count = 0;
        for (LocalDate date : allDates) {
            try {
                subsidiaryScheduleService.createOrUpdateSchedule(
                        subsidiaryId, date, openTime, closeTime, false, replaceExisting);
                count++;
            } catch (Exception e) {
                // Log do erro e continuar
                System.err.println("Error creating schedule for " + date + ": " + e.getMessage());
            }
        }

        return count;
    }

    /**
     * Cria horários recorrentes para uma sala/cadeira
     *
     * @param chairRoomId      ID da sala/cadeira
     * @param openTime         Horário de abertura
     * @param closeTime        Horário de fechamento
     * @param daysOfWeek       Dias da semana (1-7, onde 1=Segunda)
     * @param startDate        Data de início do padrão
     * @param endDate          Data de término do padrão
     * @param replaceExisting  Se deve substituir horários existentes
     * @return Número de horários criados
     */
    @Transactional
    public int createRecurringChairRoomSchedule(
            UUID chairRoomId,
            LocalTime openTime,
            LocalTime closeTime,
            List<Integer> daysOfWeek,
            LocalDate startDate,
            LocalDate endDate,
            boolean replaceExisting) {

        // Converter números (1-7) para DayOfWeek
        List<DayOfWeek> days = daysOfWeek.stream()
                .map(day -> DayOfWeek.of(day))
                .toList();

        // Lista para armazenar todas as datas geradas com base na recorrência
        List<LocalDate> allDates = new ArrayList<>();

        // Para cada dia da semana, gerar todas as datas entre startDate e endDate
        for (DayOfWeek dayOfWeek : days) {
            // Encontrar a primeira ocorrência deste dia da semana a partir da data de início
            LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));

            // Adicionar todas as ocorrências até a data final
            while (!date.isAfter(endDate)) {
                allDates.add(date);
                date = date.plusWeeks(1); // Próxima semana
            }
        }

        // Criar ou atualizar horários para cada data
        int count = 0;
        for (LocalDate date : allDates) {
            try {
                chairRoomScheduleService.createOrUpdateSchedule(
                        chairRoomId, date, openTime, closeTime, false, replaceExisting);
                count++;
            } catch (Exception e) {
                // Log do erro e continuar
                System.err.println("Error creating schedule for " + date + ": " + e.getMessage());
            }
        }

        return count;
    }

    /**
     * Cria horários recorrentes para um profissional
     *
     * @param professionalId   ID do profissional
     * @param startTime        Horário de início do trabalho
     * @param endTime          Horário de término do trabalho
     * @param daysOfWeek       Dias da semana (1-7, onde 1=Segunda)
     * @param startDate        Data de início do padrão
     * @param endDate          Data de término do padrão
     * @param replaceExisting  Se deve substituir horários existentes
     * @return Número de horários criados
     */
    @Transactional
    public int createRecurringProfessionalSchedule(
            UUID professionalId,
            LocalTime startTime,
            LocalTime endTime,
            List<Integer> daysOfWeek,
            LocalDate startDate,
            LocalDate endDate,
            boolean replaceExisting) {

        // Converter números (1-7) para DayOfWeek
        List<DayOfWeek> days = daysOfWeek.stream()
                .map(day -> DayOfWeek.of(day))
                .toList();

        // Lista para armazenar todas as datas geradas com base na recorrência
        List<LocalDate> allDates = new ArrayList<>();

        // Para cada dia da semana, gerar todas as datas entre startDate e endDate
        for (DayOfWeek dayOfWeek : days) {
            // Encontrar a primeira ocorrência deste dia da semana a partir da data de início
            LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));

            // Adicionar todas as ocorrências até a data final
            while (!date.isAfter(endDate)) {
                allDates.add(date);
                date = date.plusWeeks(1); // Próxima semana
            }
        }

        // Criar ou atualizar horários para cada data
        int count = 0;
        for (LocalDate date : allDates) {
            try {
                professionalScheduleService.createOrUpdateSchedule(
                        professionalId, date, startTime, endTime, replaceExisting);
                count++;
            } catch (Exception e) {
                // Log do erro e continuar
                System.err.println("Error creating schedule for " + date + ": " + e.getMessage());
            }
        }

        return count;
    }
}