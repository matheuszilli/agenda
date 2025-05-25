package com.agenda.app.service;

import com.agenda.app.dto.ConflictCheckRequestDTO;
import com.agenda.app.dto.ConflictCheckResponseDTO;
import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.ChairRoomScheduleEntry;
import com.agenda.app.repository.ChairRoomRepository;
import com.agenda.app.repository.ChairRoomScheduleEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para verificar conflitos de horário
 */
@Service
@RequiredArgsConstructor
public class ScheduleConflictService {

    private final ChairRoomRepository chairRoomRepository;
    private final ChairRoomScheduleEntryRepository scheduleRepository;

    /**
     * Verifica conflitos para datas específicas
     */
    @Transactional(readOnly = true)
    public ConflictCheckResponseDTO checkConflicts(ConflictCheckRequestDTO request) {
        // Verificar se a sala/cadeira existe
        ChairRoom chairRoom = chairRoomRepository.findById(request.getChairRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + request.getChairRoomId()));
        
        ConflictCheckResponseDTO response = new ConflictCheckResponseDTO(request.getChairRoomId());
        
        // Se temos datas específicas, verificamos cada uma
        if (request.getDates() != null && !request.getDates().isEmpty()) {
            checkSpecificDates(request, response);
        }
        
        // Se temos um padrão recorrente, verificamos conforme as regras
        if (request.getDaysOfWeek() != null && !request.getDaysOfWeek().isEmpty() 
                && request.getStartDate() != null && request.getEndDate() != null) {
            checkRecurringPattern(request, response);
        }
        
        return response;
    }
    
    /**
     * Verifica conflitos para datas específicas
     */
    private void checkSpecificDates(ConflictCheckRequestDTO request, ConflictCheckResponseDTO response) {
        List<LocalDate> dates = request.getDates();
        
        for (LocalDate date : dates) {
            boolean exists = scheduleRepository.existsByChairRoomIdAndDate(request.getChairRoomId(), date);
            
            if (exists) {
                // Se estamos verificando apenas customizados
                if (request.isIncludeCustomized()) {
                    ChairRoomScheduleEntry entry = scheduleRepository.findByChairRoomIdAndDate(
                            request.getChairRoomId(), date).orElse(null);
                    
                    if (entry != null && entry.isCustomized()) {
                        response.addConflict(date);
                    }
                } else {
                    // Qualquer agendamento existente é um conflito
                    response.addConflict(date);
                }
            }
        }
    }
    
    /**
     * Verifica conflitos para um padrão recorrente
     */
    private void checkRecurringPattern(ConflictCheckRequestDTO request, ConflictCheckResponseDTO response) {
        // Converter os dias da semana (0-6) para DayOfWeek
        List<DayOfWeek> daysOfWeek = request.getDaysOfWeek().stream()
                .map(day -> DayOfWeek.of(((day + 1) % 7) + 1)) // Converte 0-6 (Dom-Sáb) para 1-7 (Seg-Dom) do Java
                .collect(Collectors.toList());
        
        // Gerar todas as datas que correspondem ao padrão recorrente
        List<LocalDate> allDates = new ArrayList<>();
        for (DayOfWeek dayOfWeek : daysOfWeek) {
            LocalDate date = request.getStartDate().with(TemporalAdjusters.nextOrSame(dayOfWeek));
            while (!date.isAfter(request.getEndDate())) {
                allDates.add(date);
                date = date.plusWeeks(1);
            }
        }
        
        // Verificar cada data gerada
        for (LocalDate date : allDates) {
            boolean exists = scheduleRepository.existsByChairRoomIdAndDate(request.getChairRoomId(), date);
            
            if (exists) {
                // Se estamos verificando apenas customizados
                if (request.isIncludeCustomized()) {
                    ChairRoomScheduleEntry entry = scheduleRepository.findByChairRoomIdAndDate(
                            request.getChairRoomId(), date).orElse(null);
                    
                    if (entry != null && entry.isCustomized()) {
                        response.addConflict(date);
                    }
                } else {
                    // Qualquer agendamento existente é um conflito
                    response.addConflict(date);
                }
            }
        }
    }
}