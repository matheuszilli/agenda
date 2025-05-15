package com.agenda.app.controller;

import com.agenda.app.dto.AppointmentRequest;
import com.agenda.app.dto.AppointmentResponse;
import com.agenda.app.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody AppointmentRequest dto
    ) {
        AppointmentResponse resp = service.scheduleAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> list(
            Pageable pageable
    ) {
        Page<AppointmentResponse> page = service.listAppointments(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getOne(
            @PathVariable UUID id
    ) {
        AppointmentResponse resp = service.getAppointmentById(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/agenda")
    public ResponseEntity<Page<AppointmentResponse>> getAgenda(Pageable pageable) {
        Page<AppointmentResponse> page = service.getAgendaAppointments(pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentRequest dto
    ) {
        AppointmentResponse resp = service.updateAppointment(id, dto);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        service.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
