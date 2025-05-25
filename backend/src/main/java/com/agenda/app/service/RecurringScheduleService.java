package com.agenda.app.service;

import com.agenda.app.dto.ConflictCheckRequestDTO;
import com.agenda.app.dto.ConflictCheckResponseDTO;
import com.agenda.app.dto.DayScheduleConfigDTO;
import com.agenda.app.dto.RecurringScheduleDTO;
import com.agenda.app.exception.ScheduleConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ScheduleConflictService scheduleConflictService;

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
     * @param excludeDates     Datas específicas a serem excluídas do padrão (opcional)
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
            boolean replaceExisting,
            List<LocalDate> excludeDates) {

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
                // Verificar se a data não está na lista de exclusões
                if (excludeDates == null || !excludeDates.contains(date)) {
                    allDates.add(date);
                }
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
     * Versão simplificada sem lista de exclusões
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
        
        return createRecurringSubsidiarySchedule(
                subsidiaryId, openTime, closeTime, daysOfWeek, 
                startDate, endDate, replaceExisting, null);
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
     * @param excludeDates     Datas específicas a serem excluídas do padrão (opcional)
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
            boolean replaceExisting,
            List<LocalDate> excludeDates) {

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
                // Verificar se a data não está na lista de exclusões
                if (excludeDates == null || !excludeDates.contains(date)) {
                    allDates.add(date);
                }
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
     * Versão simplificada sem lista de exclusões
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
        
        return createRecurringChairRoomSchedule(
                chairRoomId, openTime, closeTime, daysOfWeek, 
                startDate, endDate, replaceExisting, null);
    }
    
    /**
     * Cria horários recorrentes para uma sala/cadeira com configuração avançada
     * 
     * @param recurringSchedule DTO com configuração avançada de horários recorrentes
     * @param checkConflicts Se deve verificar conflitos antes de aplicar
     * @return Lista de datas criadas
     * @throws ScheduleConflictException Se houver conflitos
     */
    @Transactional
    public List<LocalDate> createRecurringChairRoomScheduleAdvanced(
            RecurringScheduleDTO recurringSchedule, 
            boolean checkConflicts) {
        
        UUID chairRoomId = recurringSchedule.getChairRoomId();
        LocalDate startDate = recurringSchedule.getStartDate();
        LocalDate endDate = recurringSchedule.getEndDate();
        boolean replaceExisting = recurringSchedule.isReplaceExisting();
        
        // Extrair dias configurados como abertos
        List<Integer> openDays = new ArrayList<>();
        Map<Integer, LocalTime> openTimes = new HashMap<>();
        Map<Integer, LocalTime> closeTimes = new HashMap<>();
        
        for (Map.Entry<Integer, DayScheduleConfigDTO> entry : recurringSchedule.getWeekSchedule().entrySet()) {
            Integer dayIndex = entry.getKey();
            DayScheduleConfigDTO config = entry.getValue();
            
            if (config.isOpen()) {
                openDays.add(dayIndex);
                openTimes.put(dayIndex, config.getOpenTime());
                closeTimes.put(dayIndex, config.getCloseTime());
            }
        }
        
        // Verificar conflitos se solicitado
        if (checkConflicts && !replaceExisting) {
            ConflictCheckRequestDTO conflictRequest = new ConflictCheckRequestDTO();
            conflictRequest.setChairRoomId(chairRoomId);
            conflictRequest.setDaysOfWeek(openDays);
            conflictRequest.setStartDate(startDate);
            conflictRequest.setEndDate(endDate);
            conflictRequest.setIncludeCustomized(true);
            
            ConflictCheckResponseDTO conflictResponse = scheduleConflictService.checkConflicts(conflictRequest);
            
            if (conflictResponse.isHasConflicts()) {
                throw new ScheduleConflictException(
                    "Schedule conflicts detected for the recurring pattern",
                    chairRoomId,
                    conflictResponse.getConflictingDates()
                );
            }
        }
        
        // Lista para armazenar todas as datas criadas
        List<LocalDate> createdDates = new ArrayList<>();
        
        // Para cada dia configurado como aberto
        for (Integer dayIndex : openDays) {
            LocalTime openTime = openTimes.get(dayIndex);
            LocalTime closeTime = closeTimes.get(dayIndex);
            
            // Converter o índice do dia (0-6) para o formato Java (1-7)
            int javaDay = ((dayIndex + 1) % 7) + 1;
            
            // Criar horários recorrentes para este dia
            List<LocalDate> datesForDay = createDatesForDayOfWeek(
                    DayOfWeek.of(javaDay), startDate, endDate);
            
            for (LocalDate date : datesForDay) {
                try {
                    chairRoomScheduleService.createOrUpdateSchedule(
                            chairRoomId, date, openTime, closeTime, false, replaceExisting);
                    createdDates.add(date);
                } catch (Exception e) {
                    System.err.println("Error creating schedule for " + date + ": " + e.getMessage());
                }
            }
        }
        
        return createdDates;
    }
    
    /**
     * Método auxiliar para gerar todas as datas para um dia da semana específico
     */
    private List<LocalDate> createDatesForDayOfWeek(DayOfWeek dayOfWeek, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        
        while (!date.isAfter(endDate)) {
            dates.add(date);
            date = date.plusWeeks(1);
        }
        
        return dates;
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
     * @param excludeDates     Datas específicas a serem excluídas do padrão (opcional)
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
            boolean replaceExisting,
            List<LocalDate> excludeDates) {

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
                // Verificar se a data não está na lista de exclusões
                if (excludeDates == null || !excludeDates.contains(date)) {
                    allDates.add(date);
                }
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

    /**
     * Versão simplificada sem lista de exclusões
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
        
        return createRecurringProfessionalSchedule(
                professionalId, startTime, endTime, daysOfWeek, 
                startDate, endDate, replaceExisting, null);
    }

    /**
     * Criar dias fechados para uma subsidiária (feriados, férias, etc.)
     */
    @Transactional
    public int createClosedDaysForSubsidiary(UUID subsidiaryId, List<LocalDate> dates, boolean replaceExisting) {
        int count = 0;
        for (LocalDate date : dates) {
            try {
                subsidiaryScheduleService.createOrUpdateSchedule(
                        subsidiaryId, date, LocalTime.of(0, 0), LocalTime.of(0, 0), true, replaceExisting);
                count++;
            } catch (Exception e) {
                System.err.println("Error creating closed day for " + date + ": " + e.getMessage());
            }
        }
        return count;
    }

    /**
     * Criar dias fechados para uma cadeira/sala
     */
    @Transactional
    public int createClosedDaysForChairRoom(UUID chairRoomId, List<LocalDate> dates, boolean replaceExisting) {
        int count = 0;
        for (LocalDate date : dates) {
            try {
                chairRoomScheduleService.createOrUpdateSchedule(
                        chairRoomId, date, LocalTime.of(0, 0), LocalTime.of(0, 0), true, replaceExisting);
                count++;
            } catch (Exception e) {
                System.err.println("Error creating closed day for " + date + ": " + e.getMessage());
            }
        }
        return count;
    }
}