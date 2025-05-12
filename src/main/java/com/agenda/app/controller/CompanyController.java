package com.agenda.app.controller;

import com.agenda.app.dto.CompanyRequest;
import com.agenda.app.dto.CompanyResponse;
import com.agenda.app.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyRequest body) {
        var res = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}")
    public CompanyResponse get(@PathVariable UUID id) { return service.get(id); }

    @PutMapping("/{id}")
    public CompanyResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody CompanyRequest body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
