package com.agenda.app.controller;

import com.agenda.app.dto.ProfessionalChairRoomAssignmentRequest;
import com.agenda.app.dto.ProfessionalChairRoomAssignmentResponse;
import com.agenda.app.mapper.ProfessionalChairRoomAssignmentMapper;
import com.agenda.app.model.ProfessionalChairRoomAssignment;
import com.agenda.app.service.ProfessionalChairRoomAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller para gerenciar atribuições de profissionais a cadeiras/salas
 */
@RestController
@RequestMapping("/api/professional-chair-assignments")
@RequiredArgsConstructor
public class ProfessionalChairRoomAssignmentController {
    
    private final ProfessionalChairRoomAssignmentService assignmentService;
    private final ProfessionalChairRoomAssignmentMapper mapper;
    
    /**
     * Cria uma nova atribuição (única ou recorrente)
     */
    @PostMapping
    public ResponseEntity<ProfessionalChairRoomAssignmentResponse> createAssignment(
            @Valid @RequestBody ProfessionalChairRoomAssignmentRequest request) {
        
        ProfessionalChairRoomAssignment assignment;
        
        if ("single".equals(request.getAssignmentType())) {
            // Atribuição para uma data específica
            if (request.getDate() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            assignment = assignmentService.createAssignment(
                    request.getProfessionalId(),
                    request.getChairRoomId(),
                    request.getDate(),
                    request.getStartTime(),
                    request.getEndTime());
        } else if ("recurring".equals(request.getAssignmentType())) {
            // Atribuição recorrente para um dia da semana
            if (request.getDayOfWeek() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            assignment = assignmentService.createRecurringAssignment(
                    request.getProfessionalId(),
                    request.getChairRoomId(),
                    request.getDayOfWeek(),
                    request.getStartTime(),
                    request.getEndTime());
        } else if ("date-range".equals(request.getAssignmentType())) {
            // Atribuição para um intervalo de datas
            if (request.getStartDate() == null || request.getEndDate() == null || request.getDaysOfWeek() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<ProfessionalChairRoomAssignment> assignments = assignmentService.createAssignmentsForDateRange(
                    request.getProfessionalId(),
                    request.getChairRoomId(),
                    request.getDaysOfWeek(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getStartTime(),
                    request.getEndTime());
            
            // Retornar apenas a primeira atribuição
            if (assignments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            assignment = assignments.get(0);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(assignment));
    }
    
    /**
     * Busca todas as atribuições de um profissional para uma data
     */
    @GetMapping("/professional/{professionalId}/date/{date}")
    public ResponseEntity<List<ProfessionalChairRoomAssignmentResponse>> findByProfessionalAndDate(
            @PathVariable UUID professionalId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ProfessionalChairRoomAssignment> assignments = 
                assignmentService.findAssignmentsForProfessionalAndDate(professionalId, date);
        
        return ResponseEntity.ok(mapper.toResponseList(assignments));
    }
    
    /**
     * Busca todas as atribuições de uma cadeira/sala para uma data
     */
    @GetMapping("/chair-room/{chairRoomId}/date/{date}")
    public ResponseEntity<List<ProfessionalChairRoomAssignmentResponse>> findByChairRoomAndDate(
            @PathVariable UUID chairRoomId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ProfessionalChairRoomAssignment> assignments = 
                assignmentService.findAssignmentsForChairRoomAndDate(chairRoomId, date);
        
        return ResponseEntity.ok(mapper.toResponseList(assignments));
    }
    
    /**
     * Verifica se um profissional está atribuído a uma cadeira/sala em uma data/hora
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAssignment(
            @RequestParam UUID professionalId,
            @RequestParam UUID chairRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String time) {
        
        boolean isAssigned = assignmentService.isProfessionalAssignedToChairRoom(
                professionalId, chairRoomId, date, java.time.LocalTime.parse(time));
        
        return ResponseEntity.ok(isAssigned);
    }
    
    /**
     * Remove uma atribuição
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable UUID id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Remove uma atribuição específica (profissional + cadeira/sala + data)
     */
    @DeleteMapping("/specific")
    public ResponseEntity<Void> deleteSpecificAssignment(
            @RequestParam UUID professionalId,
            @RequestParam UUID chairRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        assignmentService.deleteAssignmentsForDate(professionalId, chairRoomId, date);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Remove uma atribuição recorrente (profissional + cadeira/sala + dia da semana)
     */
    @DeleteMapping("/recurring")
    public ResponseEntity<Void> deleteRecurringAssignment(
            @RequestParam UUID professionalId,
            @RequestParam UUID chairRoomId,
            @RequestParam Integer dayOfWeek) {
        
        assignmentService.deleteRecurringAssignments(professionalId, chairRoomId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }
}