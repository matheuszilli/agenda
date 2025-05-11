package com.agenda.app.controller;

import com.agenda.app.dto.*;
import com.agenda.app.service.BusinessServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class BusinessServiceController {

    private final BusinessServiceService service;

    @PostMapping
    public ResponseEntity<BusinessServiceResponse> create(
            @RequestBody @Valid BusinessServiceRequest body) {
        var respoense = service.create(body);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respoense);
    }

    @GetMapping("/{id}")
    public BusinessServiceResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public BusinessServiceResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid BusinessServiceRequest body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
