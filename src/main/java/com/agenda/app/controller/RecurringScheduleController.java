package com.agenda.app.controller;

import com.agenda.app.dto.RecurringScheduleRequest;
import com.agenda.app.service.RecurringScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para operações de criação de horários recorrentes
 */
@RestController
@RequestMapping("/api/recurring-schedules")
@RequiredArgsConstructor
public class RecurringScheduleController {

    private final RecurringScheduleService recurringScheduleService;

    /**
     * Cria horários recorrentes para uma subsidiária
     */
    @PostMapping("/subsidiary/{subsidiaryId}")
    public ResponseEntity<Integer> createRecurringSubsidiarySchedule(
            @PathVariable UUID subsidiaryId,
            @RequestBody @Valid RecurringScheduleRequest request) {

        int count = recurringScheduleService.createRecurringSubsidiarySchedule(
                subsidiaryId,
                request.getOpenTime(),
                request.getCloseTime(),
                request.getDaysOfWeek(),
                request.getStartDate(),
                request.getEndDate(),
                request.isReplaceExisting()
        );

        return ResponseEntity.ok(count);
    }

    /**
     * Cria horários recorrentes para uma sala/cadeira
     */
    @PostMapping("/chair-room/{chairRoomId}")
    public ResponseEntity<Integer> createRecurringChairRoomSchedule(
            @PathVariable UUID chairRoomId,
            @RequestBody @Valid RecurringScheduleRequest request) {

        int count = recurringScheduleService.createRecurringChairRoomSchedule(
                chairRoomId,
                request.getOpenTime(),
                request.getCloseTime(),
                request.getDaysOfWeek(),
                request.getStartDate(),
                request.getEndDate(),
                request.isReplaceExisting()
        );

        return ResponseEntity.ok(count);
    }

    /**
     * Cria horários recorrentes para um profissional
     */
    @PostMapping("/professional/{professionalId}")
    public ResponseEntity<Integer> createRecurringProfessionalSchedule(
            @PathVariable UUID professionalId,
            @RequestBody @Valid RecurringScheduleRequest request) {

        int count = recurringScheduleService.createRecurringProfessionalSchedule(
                professionalId,
                request.getOpenTime(),
                request.getCloseTime(),
                request.getDaysOfWeek(),
                request.getStartDate(),
                request.getEndDate(),
                request.isReplaceExisting()
        );

        return ResponseEntity.ok(count);
    }
}