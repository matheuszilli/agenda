package com.agenda.app.controller;

import com.agenda.app.dto.*;
import com.agenda.app.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemResponse> create(
            @RequestBody @Valid ItemRequest body) {
        var respoense = service.create(body);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respoense);
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ItemResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid ItemRequest body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
