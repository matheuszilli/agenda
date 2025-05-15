package com.agenda.app.controller;
import com.agenda.app.dto.CustomerRequest;
import com.agenda.app.dto.CustomerResponse;
import com.agenda.app.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies/{companyId}/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @PathVariable UUID companyId,
            @Valid @RequestBody CustomerRequest req
    ) {
        CustomerResponse resp = service.create(companyId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list(
            @PathVariable UUID companyId
    ) {
        List<CustomerResponse> list = service.listByCompany(companyId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getOne(
            @PathVariable UUID companyId,
            @PathVariable UUID id
    ) {
        CustomerResponse resp = service.getById(companyId, id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest req
    ) {
        CustomerResponse resp = service.update(companyId, id, req);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID companyId,
            @PathVariable UUID id
    ) {
        service.delete(companyId, id);
        return ResponseEntity.noContent().build();
    }
}

