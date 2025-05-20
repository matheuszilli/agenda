package com.agenda.app.controller;

import com.agenda.app.dto.SubsidiaryScheduleEntryRequest;
import com.agenda.app.dto.SubsidiaryScheduleEntryResponse;
import com.agenda.app.service.SubsidiaryScheduleEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subsidiary-schedules")
@RequiredArgsConstructor
public class SubsidiaryScheduleEntryController {

    private final SubsidiaryScheduleEntryService service;

    @PostMapping
    public ResponseEntity<SubsidiaryScheduleEntryResponse> create(
            @Valid @RequestBody SubsidiaryScheduleEntryRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(body));
    }

    @PutMapping("/{id}")
    public SubsidiaryScheduleEntryResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody SubsidiaryScheduleEntryRequest body) {
        return service.update(id, body);
    }

    @GetMapping("/by-subsidiary/{subsidiaryId}")
    public List<SubsidiaryScheduleEntryResponse> listBySubsidiary(@PathVariable UUID subsidiaryId) {
        return service.listBySubsidiary(subsidiaryId);
    }

    @GetMapping("/by-date")
    public SubsidiaryScheduleEntryResponse getByDate(
            @RequestParam UUID subsidiaryId,
            @RequestParam LocalDate date) {
        return service.getByDate(subsidiaryId, date);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}