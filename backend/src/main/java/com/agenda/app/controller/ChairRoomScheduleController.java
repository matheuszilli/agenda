package com.agenda.app.controller;

import com.agenda.app.dto.*;
import com.agenda.app.exception.ScheduleConflictException;
import com.agenda.app.service.ChairRoomScheduleService;
import com.agenda.app.service.RecurringScheduleService;
import com.agenda.app.service.ScheduleConflictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chair-room-schedules")
@RequiredArgsConstructor
public class ChairRoomScheduleController {

    private final ChairRoomScheduleService chairRoomScheduleService;
    private final RecurringScheduleService recurringScheduleService;
    private final ScheduleConflictService conflictService;

    @PostMapping
    public ResponseEntity<ChairRoomScheduleEntryResponse> create(
            @Valid @RequestBody ChairRoomScheduleEntryRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chairRoomScheduleService.create(request));
    }

    @GetMapping("/chair-room/{chairRoomId}")
    public ResponseEntity<List<ChairRoomScheduleEntryResponse>> getByChairRoom(
            @PathVariable UUID chairRoomId) {
        return ResponseEntity.ok(chairRoomScheduleService.getByChairRoomId(chairRoomId));
    }

    @GetMapping("/chair-room/{chairRoomId}/date/{date}")
    public ResponseEntity<ChairRoomScheduleEntryResponse> getByChairRoomAndDate(
            @PathVariable UUID chairRoomId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(chairRoomScheduleService.getByChairRoomAndDate(chairRoomId, date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChairRoomScheduleEntryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ChairRoomScheduleEntryRequest request) {
        return ResponseEntity.ok(chairRoomScheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        chairRoomScheduleService.delete(id);
    }

    /**
     * Endpoint para verificar conflitos potenciais
     */
    @PostMapping("/conflicts-check")
    public ResponseEntity<ConflictCheckResponseDTO> checkConflicts(
            @Valid @RequestBody ConflictCheckRequestDTO request) {
        return ResponseEntity.ok(conflictService.checkConflicts(request));
    }

    /**
     * Endpoint para criar exceções (agendamentos customizados para dias específicos)
     */
    @PostMapping("/exception")
    public ResponseEntity<ChairRoomScheduleEntryResponse> createException(
            @Valid @RequestBody ExceptionScheduleDTO request) {
        
        ChairRoomScheduleEntryRequest scheduleRequest = new ChairRoomScheduleEntryRequest();
        scheduleRequest.setChairRoomId(request.getChairRoomId());
        scheduleRequest.setDate(request.getDate());
        scheduleRequest.setOpenTime(request.getOpenTime());
        scheduleRequest.setCloseTime(request.getCloseTime());
        scheduleRequest.setClosed(request.isClosed());
        
        try {
            // Se existe e deve substituir, atualiza; se não existe, cria
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(chairRoomScheduleService.createOrUpdateSchedule(
                            request.getChairRoomId(),
                            request.getDate(),
                            request.getOpenTime(),
                            request.getCloseTime(),
                            request.isClosed(),
                            request.isReplaceExisting()));
        } catch (ScheduleConflictException e) {
            // Retornar conflito com as datas conflitantes
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Endpoint unificado para criar agendamentos recorrentes
     */
    @PostMapping("/recurring")
    public ResponseEntity<List<ChairRoomScheduleEntryResponse>> createRecurring(
            @Valid @RequestBody RecurringScheduleDTO request,
            @RequestParam(defaultValue = "true") boolean checkConflicts) {
        
        try {
            // Usar o serviço avançado para criar os horários recorrentes
            List<LocalDate> createdDates = recurringScheduleService.createRecurringChairRoomScheduleAdvanced(
                    request, checkConflicts);
            
            // Buscar todos os agendamentos criados
            List<ChairRoomScheduleEntryResponse> responses = new ArrayList<>();
            for (LocalDate date : createdDates) {
                try {
                    ChairRoomScheduleEntryResponse response = chairRoomScheduleService
                            .getByChairRoomAndDate(request.getChairRoomId(), date);
                    responses.add(response);
                } catch (Exception e) {
                    // Ignorar erros ao buscar (pode ser que o agendamento não tenha sido criado)
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (ScheduleConflictException e) {
            // Em caso de conflitos, retornar status CONFLICT
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            // Para outros erros, retornar BAD_REQUEST
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    /**
     * Endpoint legado para compatibilidade com o frontend atual
     * Será depreciado em versões futuras
     */
    @PostMapping("/recurring-advanced")
    public ResponseEntity<List<ChairRoomScheduleEntryResponse>> createRecurringAdvanced(
            @RequestParam UUID chairRoomId,
            @RequestBody Map<String, Object> scheduleData,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "false") boolean replaceExisting) {

        try {
            // Converter os dados do frontend para o novo formato
            RecurringScheduleDTO recurringScheduleDTO = new RecurringScheduleDTO();
            recurringScheduleDTO.setChairRoomId(chairRoomId);
            recurringScheduleDTO.setStartDate(startDate);
            recurringScheduleDTO.setEndDate(endDate);
            recurringScheduleDTO.setReplaceExisting(replaceExisting);
            
            // Extrair a configuração de dias da semana
            Map<String, Map<String, Object>> weekScheduleMap = 
                    (Map<String, Map<String, Object>>) scheduleData.get("weekSchedule");
            
            if (weekScheduleMap != null) {
                Map<Integer, DayScheduleConfigDTO> weekSchedule = weekScheduleMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> Integer.parseInt(entry.getKey()),
                                entry -> {
                                    Map<String, Object> config = entry.getValue();
                                    DayScheduleConfigDTO dayConfig = new DayScheduleConfigDTO();
                                    dayConfig.setOpen((Boolean) config.getOrDefault("open", true));
                                    dayConfig.setOpenTime(LocalTime.parse((String) config.get("openTime")));
                                    dayConfig.setCloseTime(LocalTime.parse((String) config.get("closeTime")));
                                    return dayConfig;
                                }
                        ));
                
                recurringScheduleDTO.setWeekSchedule(weekSchedule);
            }
            
            // Usar o novo endpoint para criar os horários recorrentes
            List<LocalDate> createdDates = recurringScheduleService.createRecurringChairRoomScheduleAdvanced(
                    recurringScheduleDTO, false);
            
            // Buscar todos os agendamentos criados
            List<ChairRoomScheduleEntryResponse> responses = new ArrayList<>();
            for (LocalDate date : createdDates) {
                try {
                    ChairRoomScheduleEntryResponse response = chairRoomScheduleService
                            .getByChairRoomAndDate(chairRoomId, date);
                    responses.add(response);
                } catch (Exception e) {
                    // Ignorar erros ao buscar
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }
}