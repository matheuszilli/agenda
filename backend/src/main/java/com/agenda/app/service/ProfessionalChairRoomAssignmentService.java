package com.agenda.app.service;

import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.Professional;
import com.agenda.app.model.ProfessionalChairRoomAssignment;
import com.agenda.app.repository.ChairRoomRepository;
import com.agenda.app.repository.ProfessionalChairRoomAssignmentRepository;
import com.agenda.app.repository.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para gerenciar as atribuições de profissionais a cadeiras/salas
 */
@Service
@RequiredArgsConstructor
public class ProfessionalChairRoomAssignmentService {
    
    private final ProfessionalChairRoomAssignmentRepository assignmentRepo;
    private final ProfessionalRepository professionalRepo;
    private final ChairRoomRepository chairRoomRepo;
    
    /**
     * Cria uma atribuição única de profissional a uma cadeira/sala para uma data específica
     */
    @Transactional
    public ProfessionalChairRoomAssignment createAssignment(
            UUID professionalId,
            UUID chairRoomId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        
        // Buscar entidades relacionadas
        Professional professional = professionalRepo.findById(professionalId)
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + professionalId));
        
        ChairRoom chairRoom = chairRoomRepo.findById(chairRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + chairRoomId));
        
        // Verificar se já existe uma atribuição para este profissional, cadeira e data
        Optional<ProfessionalChairRoomAssignment> existingAssignment = 
                assignmentRepo.findByProfessionalIdAndChairRoomIdAndDate(professionalId, chairRoomId, date);
        
        if (existingAssignment.isPresent()) {
            ProfessionalChairRoomAssignment assignment = existingAssignment.get();
            assignment.setStartTime(startTime);
            assignment.setEndTime(endTime);
            return assignmentRepo.save(assignment);
        } else {
            // Criar nova atribuição
            ProfessionalChairRoomAssignment assignment = 
                    ProfessionalChairRoomAssignment.createSingleDay(
                            professional, chairRoom, date, startTime, endTime);
            
            return assignmentRepo.save(assignment);
        }
    }
    
    /**
     * Cria atribuições recorrentes de profissional a uma cadeira/sala para um dia da semana
     */
    @Transactional
    public ProfessionalChairRoomAssignment createRecurringAssignment(
            UUID professionalId,
            UUID chairRoomId,
            Integer dayOfWeek,
            LocalTime startTime,
            LocalTime endTime) {
        
        // Validar dia da semana
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 (Monday) and 7 (Sunday)");
        }
        
        // Buscar entidades relacionadas
        Professional professional = professionalRepo.findById(professionalId)
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + professionalId));
        
        ChairRoom chairRoom = chairRoomRepo.findById(chairRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + chairRoomId));
        
        // Verificar se já existe uma atribuição recorrente para este profissional, cadeira e dia da semana
        Optional<ProfessionalChairRoomAssignment> existingAssignment = 
                assignmentRepo.findByProfessional_IdAndChairRoom_IdAndRecurringTrueAndDayOfWeek(
                        professionalId, chairRoomId, dayOfWeek);
        
        if (existingAssignment.isPresent()) {
            ProfessionalChairRoomAssignment assignment = existingAssignment.get();
            assignment.setStartTime(startTime);
            assignment.setEndTime(endTime);
            return assignmentRepo.save(assignment);
        } else {
            // Criar nova atribuição recorrente
            ProfessionalChairRoomAssignment assignment = 
                    ProfessionalChairRoomAssignment.createRecurring(
                            professional, chairRoom, dayOfWeek, startTime, endTime);
            
            return assignmentRepo.save(assignment);
        }
    }
    
    /**
     * Cria atribuições recorrentes para vários dias da semana
     */
    @Transactional
    public List<ProfessionalChairRoomAssignment> createRecurringAssignments(
            UUID professionalId,
            UUID chairRoomId,
            List<Integer> daysOfWeek,
            LocalTime startTime,
            LocalTime endTime) {
        
        List<ProfessionalChairRoomAssignment> createdAssignments = new ArrayList<>();
        
        for (Integer dayOfWeek : daysOfWeek) {
            ProfessionalChairRoomAssignment assignment = createRecurringAssignment(
                    professionalId, chairRoomId, dayOfWeek, startTime, endTime);
            
            createdAssignments.add(assignment);
        }
        
        return createdAssignments;
    }
    
    /**
     * Cria atribuições para um período de datas, seguindo um padrão de dias da semana
     */
    @Transactional
    public List<ProfessionalChairRoomAssignment> createAssignmentsForDateRange(
            UUID professionalId,
            UUID chairRoomId,
            List<Integer> daysOfWeek,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime) {
        
        List<ProfessionalChairRoomAssignment> createdAssignments = new ArrayList<>();
        
        // Buscar entidades relacionadas
        Professional professional = professionalRepo.findById(professionalId)
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + professionalId));
        
        ChairRoom chairRoom = chairRoomRepo.findById(chairRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + chairRoomId));
        
        // Para cada dia da semana, gerar todas as datas entre startDate e endDate
        for (Integer dayOfWeekInt : daysOfWeek) {
            DayOfWeek dayOfWeek = DayOfWeek.of(dayOfWeekInt);
            
            // Encontrar a primeira ocorrência deste dia da semana a partir da data de início
            LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
            
            // Adicionar todas as ocorrências até a data final
            while (!date.isAfter(endDate)) {
                ProfessionalChairRoomAssignment assignment = createAssignment(
                        professionalId, chairRoomId, date, startTime, endTime);
                
                createdAssignments.add(assignment);
                
                date = date.plusWeeks(1); // Próxima semana
            }
        }
        
        return createdAssignments;
    }
    
    /**
     * Verifica se um profissional está atribuído a uma cadeira/sala em uma data/hora específica
     */
    @Transactional(readOnly = true)
    public boolean isProfessionalAssignedToChairRoom(
            UUID professionalId, 
            UUID chairRoomId,
            LocalDate date,
            LocalTime time) {
        
        // Buscar atribuições específicas para a data
        List<ProfessionalChairRoomAssignment> dateAssignments = 
                assignmentRepo.findByProfessionalIdAndChairRoomIdAndDate(professionalId, chairRoomId, date)
                        .map(List::of)
                        .orElse(List.of());
        
        // Se não houver atribuições específicas, buscar atribuições recorrentes
        if (dateAssignments.isEmpty()) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            dateAssignments = assignmentRepo
                    .findByProfessional_IdAndChairRoom_IdAndRecurringTrueAndDayOfWeek(professionalId, chairRoomId, dayOfWeek)
                    .map(List::of)
                    .orElse(List.of());
        }
        
        // Verificar se alguma atribuição cobre o horário especificado
        return dateAssignments.stream()
                .anyMatch(assignment -> 
                        !time.isBefore(assignment.getStartTime()) && !time.isAfter(assignment.getEndTime()));
    }
    
    /**
     * Busca todas as atribuições de um profissional para uma data específica
     */
    @Transactional(readOnly = true)
    public List<ProfessionalChairRoomAssignment> findAssignmentsForProfessionalAndDate(
            UUID professionalId, LocalDate date) {
        
        // Buscar atribuições específicas para a data
        List<ProfessionalChairRoomAssignment> specificAssignments = 
                assignmentRepo.findByProfessional_IdAndDate(professionalId, date);
        
        // Se não houver atribuições específicas, buscar atribuições recorrentes
        if (specificAssignments.isEmpty()) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            return assignmentRepo.findByChairRoom_IdAndRecurringTrueAndDayOfWeek(professionalId, dayOfWeek);
        }
        
        return specificAssignments;
    }
    
    /**
     * Busca todas as atribuições de uma cadeira/sala para uma data específica
     */
    @Transactional(readOnly = true)
    public List<ProfessionalChairRoomAssignment> findAssignmentsForChairRoomAndDate(
            UUID chairRoomId, LocalDate date) {
        
        // Buscar atribuições específicas para a data
        List<ProfessionalChairRoomAssignment> specificAssignments = 
                assignmentRepo.findByChairRoom_IdAndDate(chairRoomId, date);
        
        // Se não houver atribuições específicas, buscar atribuições recorrentes
        if (specificAssignments.isEmpty()) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            return assignmentRepo.findByChairRoom_IdAndRecurringTrueAndDayOfWeek(chairRoomId, dayOfWeek);
        }
        
        return specificAssignments;
    }
    
    /**
     * Remove uma atribuição específica
     */
    @Transactional
    public void deleteAssignment(UUID assignmentId) {
        assignmentRepo.deleteById(assignmentId);
    }
    
    /**
     * Remove todas as atribuições de um profissional a uma cadeira/sala em uma data específica
     */
    @Transactional
    public void deleteAssignmentsForDate(UUID professionalId, UUID chairRoomId, LocalDate date) {
        assignmentRepo.findByProfessionalIdAndChairRoomIdAndDate(professionalId, chairRoomId, date)
                .ifPresent(assignmentRepo::delete);
    }
    
    /**
     * Remove todas as atribuições recorrentes de um profissional a uma cadeira/sala em um dia da semana
     */
    @Transactional
    public void deleteRecurringAssignments(UUID professionalId, UUID chairRoomId, Integer dayOfWeek) {
        assignmentRepo.findByProfessional_IdAndChairRoom_IdAndRecurringTrueAndDayOfWeek(professionalId, chairRoomId, dayOfWeek)
                .ifPresent(assignmentRepo::delete);
    }
}