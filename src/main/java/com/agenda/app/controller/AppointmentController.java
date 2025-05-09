package com.agenda.app.controller;

import com.agenda.app.dto.AppointmentRequest;
import com.agenda.app.dto.AppointmentResponse;
import com.agenda.app.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody AppointmentRequest dto) {
        var resp = service.scheduleAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
