package com.agenda.app.controller;

import com.agenda.app.dto.ProfessionalScheduleEntryRequest;
import com.agenda.app.dto.ProfessionalScheduleEntryResponse;
import com.agenda.app.service.ProfessionalScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/professionals/schedule")
@RequiredArgsConstructor
public class ProfessionalScheduleController {

    private final ProfessionalScheduleService service;

    @PostMapping
    public ResponseEntity<ProfessionalScheduleEntryResponse> create(
            @RequestBody @Valid ProfessionalScheduleEntryRequest body
    ) {
        var res = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{professionalId}")
    public List<ProfessionalScheduleEntryResponse> list(
            @PathVariable UUID professionalId
    ) {
        return service.listByProfessional(professionalId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}