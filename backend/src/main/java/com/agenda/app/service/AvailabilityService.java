package com.agenda.app.service;

import com.agenda.app.model.AppointmentStatus;
import com.agenda.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável por verificar disponibilidade de subsidiárias, 
 * profissionais e cadeiras/salas para agendamentos.
 */
@Service
@RequiredArgsConstructor
public class AvailabilityService {
    
    private final SubsidiaryScheduleEntryRepository subsidiaryScheduleRepo;
    private final ChairRoomScheduleEntryRepository chairRoomScheduleRepo;
    private final ProfessionalScheduleEntryRepository professionalScheduleRepo;
    private final ProfessionalChairRoomAssignmentRepository assignmentRepo;
    private final AppointmentRepository appointmentRepo;
    
    /**
     * Verifica se uma subsidiária está aberta em uma data/hora específica
     */
    @Transactional(readOnly = true)
    public boolean isSubsidiaryOpen(UUID subsidiaryId, LocalDate date, LocalTime time) {
        return subsidiaryScheduleRepo.findBySubsidiaryIdAndDate(subsidiaryId, date)
                .stream()
                .findFirst()
                .filter(entry -> !entry.isClosed())
                .filter(entry -> !time.isBefore(entry.getOpenTime()) && !time.isAfter(entry.getCloseTime()))
                .isPresent();
    }
    
    /**
     * Verifica se uma cadeira/sala está disponível em uma data/hora específica
     */
    @Transactional(readOnly = true)
    public boolean isChairRoomAvailable(UUID chairRoomId, LocalDate date, 
                                       LocalTime startTime, LocalTime endTime) {
        // Verificar se a cadeira/sala tem horário configurado para a data
        var scheduleOpt = chairRoomScheduleRepo.findByChairRoomIdAndDate(chairRoomId, date);
        if (scheduleOpt.isEmpty() || scheduleOpt.get().isClosed()) {
            return false;
        }
        
        var schedule = scheduleOpt.get();
        
        // Verificar se o horário solicitado está dentro do horário de funcionamento
        if (startTime.isBefore(schedule.getOpenTime()) || endTime.isAfter(schedule.getCloseTime())) {
            return false;
        }
        
        // Verificar se não há agendamentos que conflitem
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);
        
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getChairRoom() != null && a.getChairRoom().getId().equals(chairRoomId))
                .filter(a -> !a.getStatus().equals(AppointmentStatus.CANCELLED))
                .noneMatch(a -> !(a.getEndTime().isBefore(start) || a.getStartTime().isAfter(end)));
    }
    
    /**
     * Verifica se um profissional está disponível em uma data/hora específica
     */
    @Transactional(readOnly = true)
    public boolean isProfessionalAvailable(UUID professionalId, LocalDate date, 
                                          LocalTime startTime, LocalTime endTime) {
        // Verificar se o profissional tem horário configurado para a data
        var scheduleOpt = professionalScheduleRepo.findFirstByProfessionalIdAndDate(professionalId, date);
        if (scheduleOpt.isEmpty()) {
            return false;
        }
        
        var schedule = scheduleOpt.get();
        
        // Verificar se o horário solicitado está dentro do horário de trabalho
        if (startTime.isBefore(schedule.getStartTime()) || endTime.isAfter(schedule.getEndTime())) {
            return false;
        }
        
        // Verificar se não há agendamentos que conflitem
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);
        
        return appointmentRepo.findByProfessionalIdAndStartTimeAfterAndEndTimeBefore(
                        professionalId, 
                        start.minus(1, ChronoUnit.DAYS), 
                        end.plus(1, ChronoUnit.DAYS))
                .stream()
                .filter(a -> !a.getStatus().equals(AppointmentStatus.CANCELLED))
                .noneMatch(a -> !(a.getEndTime().isBefore(start) || a.getStartTime().isAfter(end)));
    }
    
    /**
     * Encontra slots disponíveis para um profissional, cadeira/sala e serviço em um período de datas
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAvailableSlots(
            UUID subsidiaryId, 
            UUID itemId,
            UUID professionalId, 
            UUID chairRoomId,
            LocalDate startDate, 
            LocalDate endDate,
            Integer durationMinutes) {
        
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        
        // Para cada data no intervalo
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            final LocalDate date = currentDate;
            
            // Verificar se a subsidiária está aberta
            var subsidiaryScheduleOpt = subsidiaryScheduleRepo.findBySubsidiaryIdAndDate(subsidiaryId, date)
                    .stream()
                    .findFirst();
            
            if (subsidiaryScheduleOpt.isPresent() && !subsidiaryScheduleOpt.get().isClosed()) {
                var subsidiarySchedule = subsidiaryScheduleOpt.get();
                
                // Verificar disponibilidade do profissional
                var professionalScheduleOpt = professionalScheduleRepo.findFirstByProfessionalIdAndDate(professionalId, date);
                
                if (professionalScheduleOpt.isPresent()) {
                    var professionalSchedule = professionalScheduleOpt.get();
                    
                    // Determinar horário efetivo (interseção entre subsidiária e profissional)
                    LocalTime effectiveStart = professionalSchedule.getStartTime().isAfter(subsidiarySchedule.getOpenTime()) 
                            ? professionalSchedule.getStartTime() 
                            : subsidiarySchedule.getOpenTime();
                    
                    LocalTime effectiveEnd = professionalSchedule.getEndTime().isBefore(subsidiarySchedule.getCloseTime()) 
                            ? professionalSchedule.getEndTime() 
                            : subsidiarySchedule.getCloseTime();
                    
                    // Se cadeira/sala for especificada, verificar também sua disponibilidade
                    if (chairRoomId != null) {
                        var chairRoomScheduleOpt = chairRoomScheduleRepo.findByChairRoomIdAndDate(chairRoomId, date);
                        
                        if (chairRoomScheduleOpt.isPresent() && !chairRoomScheduleOpt.get().isClosed()) {
                            var chairRoomSchedule = chairRoomScheduleOpt.get();
                            
                            // Ajustar horário efetivo considerando também a cadeira/sala
                            if (chairRoomSchedule.getOpenTime().isAfter(effectiveStart)) {
                                effectiveStart = chairRoomSchedule.getOpenTime();
                            }
                            
                            if (chairRoomSchedule.getCloseTime().isBefore(effectiveEnd)) {
                                effectiveEnd = chairRoomSchedule.getCloseTime();
                            }
                        } else {
                            // Se a cadeira não estiver disponível nesta data, pular para a próxima data
                            currentDate = currentDate.plusDays(1);
                            continue;
                        }
                    }
                    
                    // Buscar agendamentos existentes para verificar conflitos
                    List<Map<String, Object>> busySlots = getBusySlots(professionalId, chairRoomId, date);
                    
                    // Gerar slots disponíveis com intervalos de 'durationMinutes'
                    LocalTime slotStart = effectiveStart;
                    while (slotStart.plus(durationMinutes, ChronoUnit.MINUTES).isBefore(effectiveEnd) 
                            || slotStart.plus(durationMinutes, ChronoUnit.MINUTES).equals(effectiveEnd)) {
                        
                        final LocalTime currentSlotStart = slotStart;
                        LocalTime slotEnd = slotStart.plus(durationMinutes, ChronoUnit.MINUTES);
                        
                        // Verificar se o slot não conflita com agendamentos existentes
                        boolean isAvailable = busySlots.stream()
                                .noneMatch(busy -> {
                                    LocalTime busyStart = (LocalTime) busy.get("startTime");
                                    LocalTime busyEnd = (LocalTime) busy.get("endTime");
                                    return !(slotEnd.isBefore(busyStart) || currentSlotStart.isAfter(busyEnd));
                                });
                        
                        if (isAvailable) {
                            Map<String, Object> slot = new HashMap<>();
                            slot.put("date", date);
                            slot.put("startTime", currentSlotStart);
                            slot.put("endTime", slotEnd);
                            slot.put("professionalId", professionalId);
                            if (chairRoomId != null) {
                                slot.put("chairRoomId", chairRoomId);
                            }
                            
                            availableSlots.add(slot);
                        }
                        
                        // Avançar para o próximo slot
                        slotStart = slotStart.plus(30, ChronoUnit.MINUTES); // Incrementos de 30 minutos
                    }
                }
            }
            
            // Avançar para a próxima data
            currentDate = currentDate.plusDays(1);
        }
        
        return availableSlots;
    }
    
    /**
     * Método auxiliar para obter os slots ocupados (agendamentos existentes)
     */
    private List<Map<String, Object>> getBusySlots(UUID professionalId, UUID chairRoomId, LocalDate date) {
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
        
        // Buscar agendamentos do profissional nesta data
        return appointmentRepo.findByProfessionalIdAndStartTimeAfterAndEndTimeBefore(
                        professionalId, dayStart, dayEnd)
                .stream()
                .filter(a -> !a.getStatus().equals(AppointmentStatus.CANCELLED))
                .filter(a -> chairRoomId == null || 
                        (a.getChairRoom() != null && a.getChairRoom().getId().equals(chairRoomId)))
                .map(a -> {
                    Map<String, Object> slot = new HashMap<>();
                    slot.put("startTime", a.getStartTime().toLocalTime());
                    slot.put("endTime", a.getEndTime().toLocalTime());
                    return slot;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Retorna o dia da semana para uma data (1 = Segunda, 7 = Domingo)
     */
    private int getDayOfWeek(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // Converter do formato Java (1 = Segunda, 7 = Domingo) para o formato usado no banco
        return dayOfWeek.getValue();
    }
}