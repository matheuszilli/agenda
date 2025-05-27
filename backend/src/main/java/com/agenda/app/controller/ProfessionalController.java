package com.agenda.app.controller;

import com.agenda.app.dto.*;
import com.agenda.app.service.ProfessionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.version}/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService service;

    @PostMapping
    public ResponseEntity<ProfessionalResponse> create(
            @RequestBody @Valid ProfessionalRequest body) {
        System.out.println("ENDEREÇO: " + body.getAddress());
        System.out.println("CIDADE: " + (body.getAddress() != null ? body.getAddress().getCity() : "nulo"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(body));
    }

    @GetMapping("/{id}")
    public ProfessionalResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<ItemResponse>> getServices(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getServicesByProfessional(id));
    }

    @GetMapping("/by-subsidiary/{id}")
    public List<ProfessionalResponse> listBySubsidiary(@PathVariable UUID id) {
        return service.listBySubsidiary(id);
    }

    @GetMapping
    public List<ProfessionalResponse> listAll() {
        return service.listAll();
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