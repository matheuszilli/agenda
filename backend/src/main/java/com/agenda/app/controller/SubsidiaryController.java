// src/main/java/com/agenda/app/controller/SubsidiaryController.java
package com.agenda.app.controller;

import com.agenda.app.dto.SubsidiaryRequest;
import com.agenda.app.dto.SubsidiaryResponse;
import com.agenda.app.service.SubsidiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/subsidiaries")
@RequiredArgsConstructor
public class SubsidiaryController {

    private final SubsidiaryService service;

    @PostMapping
    public ResponseEntity<SubsidiaryResponse> create(@RequestBody @Valid SubsidiaryRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body));
    }

    @GetMapping("/{id}")
    public SubsidiaryResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public ResponseEntity<List<SubsidiaryResponse>> listAll() {
        List<SubsidiaryResponse> subsidiaries = service.listAll();
        return ResponseEntity.ok(subsidiaries);
    }
    
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<SubsidiaryResponse>> listByCompany(@PathVariable UUID companyId) {
        List<SubsidiaryResponse> subsidiaries = service.listByCompany(companyId);
        return ResponseEntity.ok(subsidiaries);
    }

    @PutMapping("/{id}")
    public SubsidiaryResponse update(@PathVariable UUID id,
                                     @RequestBody @Valid SubsidiaryRequest body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
