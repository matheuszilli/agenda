package com.agenda.app.controller;

import com.agenda.app.dto.AvailabilityRequest;
import com.agenda.app.dto.AvailabilityResponse;
import com.agenda.app.repository.ItemRepository;
import com.agenda.app.repository.ChairRoomRepository;
import com.agenda.app.repository.ProfessionalRepository;
import com.agenda.app.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller para operações relacionadas à disponibilidade
 */
@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final ProfessionalRepository professionalRepository;
    private final ChairRoomRepository chairRoomRepository;
    private final ItemRepository itemRepository;
    
    /**
     * Verifica a disponibilidade de uma subsidiária em uma data/hora específica
     */
    @GetMapping("/subsidiary")
    public ResponseEntity<Boolean> checkSubsidiaryAvailability(
            @RequestParam UUID subsidiaryId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time) {
        
        boolean isAvailable = availabilityService.isSubsidiaryOpen(subsidiaryId, date, time);
        return ResponseEntity.ok(isAvailable);
    }
    
    /**
     * Verifica a disponibilidade de uma cadeira/sala em uma data/hora específica
     */
    @GetMapping("/chair-room")
    public ResponseEntity<Boolean> checkChairRoomAvailability(
            @RequestParam UUID chairRoomId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime) {
        
        boolean isAvailable = availabilityService.isChairRoomAvailable(chairRoomId, date, startTime, endTime);
        return ResponseEntity.ok(isAvailable);
    }
    
    /**
     * Verifica a disponibilidade de um profissional em uma data/hora específica
     */
    @GetMapping("/professional")
    public ResponseEntity<Boolean> checkProfessionalAvailability(
            @RequestParam UUID professionalId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime) {
        
        boolean isAvailable = availabilityService.isProfessionalAvailable(professionalId, date, startTime, endTime);
        return ResponseEntity.ok(isAvailable);
    }
    
    /**
     * Busca slots disponíveis para agendamento
     */
    @PostMapping("/slots")
    public ResponseEntity<List<AvailabilityResponse>> findAvailableSlots(
            @Valid @RequestBody AvailabilityRequest request) {
        
        List<Map<String, Object>> slots = availabilityService.findAvailableSlots(
                request.getSubsidiaryId(),
                request.getItemId(),
                request.getProfessionalId(),
                request.getChairRoomId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDurationMinutes()
        );
        
        // Converter para DTO de resposta
        List<AvailabilityResponse> response = slots.stream()
                .map(slot -> {
                    AvailabilityResponse availabilitySlot = new AvailabilityResponse();
                    availabilitySlot.setDate((LocalDate) slot.get("date"));
                    availabilitySlot.setStartTime((LocalTime) slot.get("startTime"));
                    availabilitySlot.setEndTime((LocalTime) slot.get("endTime"));
                    availabilitySlot.setSubsidiaryId(request.getSubsidiaryId());
                    availabilitySlot.setProfessionalId(request.getProfessionalId());
                    availabilitySlot.setChairRoomId(request.getChairRoomId());
                    availabilitySlot.setItemId(request.getItemId());
                    availabilitySlot.setDurationMinutes(request.getDurationMinutes());
                    
                    // Adicionar informações adicionais para exibição
                    professionalRepository.findById(request.getProfessionalId())
                            .ifPresent(professional -> 
                                    availabilitySlot.setProfessionalName(professional.getFullName()));
                    
                    if (request.getChairRoomId() != null) {
                        chairRoomRepository.findById(request.getChairRoomId())
                                .ifPresent(chairRoom -> 
                                        availabilitySlot.setChairRoomName(chairRoom.getName()));
                    }
                    
                    itemRepository.findById(request.getItemId())
                            .ifPresent(item -> 
                                    availabilitySlot.setItemName(item.getName()));
                    
                    return availabilitySlot;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}