package com.agenda.app.controller;

import com.agenda.app.dto.*;
import com.agenda.app.service.ProfessionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService service;

    @PostMapping
    public ResponseEntity<ProfessionalResponse> create(
            @RequestBody @Valid ProfessionalRequest body) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(body));
    }

    @GetMapping("/{id}")
    public ProfessionalResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ProfessionalResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid ProfessionalRequest body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}