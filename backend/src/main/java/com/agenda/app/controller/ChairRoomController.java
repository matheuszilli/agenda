package com.agenda.app.controller;


import com.agenda.app.service.ChairRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.agenda.app.dto.ChairRoomResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/chair-rooms")
@RequiredArgsConstructor
public class ChairRoomController {

    private final ChairRoomService chairRoomService;

    @GetMapping
    public ResponseEntity<Page<ChairRoomResponse>> getAllChairRooms(Pageable pageable) {
        return ResponseEntity.ok(chairRoomService.getAllChairRooms(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChairRoomResponse> getChairRoomById(@PathVariable UUID id) {
        return ResponseEntity.ok(chairRoomService.getChairRoomById(id));
    }

    @GetMapping("/subsidiary/{subsidiaryId}")
    public ResponseEntity<List<ChairRoomResponse>> getChairRoomsBySubsidiary(@PathVariable UUID subsidiaryId) {
        return ResponseEntity.ok(chairRoomService.getChairRoomsBySubsidiary(subsidiaryId));
    }

    @GetMapping(  "/available")
    public ResponseEntity<List<ChairRoomResponse>> getAvailableRoomsForTimeSlot(
            @RequestParam UUID subsidiaryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        return ResponseEntity.ok(chairRoomService.findAvailableRoomsForTimeSlot(subsidiaryId, startTime, endTime));
    }


}
