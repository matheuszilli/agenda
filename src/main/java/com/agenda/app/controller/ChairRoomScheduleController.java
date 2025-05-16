package com.agenda.app.controller;

import com.agenda.app.dto.ChairRoomScheduleEntryRequest;
import com.agenda.app.dto.ChairRoomScheduleEntryResponse;
import com.agenda.app.service.ChairRoomScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chair-room-schedules")
@RequiredArgsConstructor
public class ChairRoomScheduleController {

    private final ChairRoomScheduleService service;

    @PostMapping
    public ResponseEntity<ChairRoomScheduleEntryResponse> create(
            @Valid @RequestBody ChairRoomScheduleEntryRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping("/chair-room/{chairRoomId}")
    public ResponseEntity<List<ChairRoomScheduleEntryResponse>> getByChairRoom(
            @PathVariable UUID chairRoomId) {
        return ResponseEntity.ok(service.getByChairRoomId(chairRoomId));
    }

    @GetMapping("/chair-room/{chairRoomId}/date/{date}")
    public ResponseEntity<ChairRoomScheduleEntryResponse> getByChairRoomAndDate(
            @PathVariable UUID chairRoomId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getByChairRoomAndDate(chairRoomId, date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChairRoomScheduleEntryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ChairRoomScheduleEntryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/recurring")
    public ResponseEntity<List<ChairRoomScheduleEntryResponse>> createRecurring(
            @RequestParam UUID chairRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime openTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime closeTime,
            @RequestParam List<Integer> daysOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "false") boolean replaceExisting) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createRecurring(chairRoomId, daysOfWeek, openTime, closeTime, startDate, endDate, replaceExisting));
    }
}