package com.agenda.app.service;


import com.agenda.app.dto.ChairRoomRequest;
import com.agenda.app.dto.ChairRoomResponse;
import com.agenda.app.mapper.ChairRoomMapper;
import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.repository.ChairRoomRepository;
import com.agenda.app.repository.ProfessionalRepository;
import com.agenda.app.repository.SubsidiaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChairRoomService {

    private final ChairRoomRepository chairRoomRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final ProfessionalRepository professionalRepository;
    private final ChairRoomMapper mapper;

    @Transactional(readOnly = true)
    public Page<ChairRoomResponse> getAllChairRooms(Pageable pageable) {
        return chairRoomRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ChairRoomResponse getChairRoomById(UUID id) {
        return chairRoomRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Chair room not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ChairRoomResponse> getChairRoomsBySubsidiary(UUID subsidiaryId) {
        List<ChairRoom> rooms = chairRoomRepository.findBySubsidiaryId(subsidiaryId);
        return rooms.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChairRoomResponse createChairRoom(ChairRoomRequest request) {
        Subsidiary subsidiary = subsidiaryRepository.findById(request.getSubsidiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found with ID: " + request.getSubsidiaryId()));

        ChairRoom chairRoom = mapper.toEntity(request, subsidiary);
        chairRoom = chairRoomRepository.save(chairRoom);

        return mapper.toResponse(chairRoom);
    }

    @Transactional
    public ChairRoomResponse updateChairRoom(UUID id, ChairRoomRequest request) {
        ChairRoom chairRoom = chairRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChairRoom not found with ID: " + id));

        mapper.updateFromRequest(request, chairRoom);

        if (request.getSubsidiaryId() != null &&
                !request.getSubsidiaryId().equals(chairRoom.getSubsidiary().getId())) {
            Subsidiary subsidiary = subsidiaryRepository.findById(request.getSubsidiaryId())
                    .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found with ID: " + request.getSubsidiaryId()));
            chairRoom.setSubsidiary(subsidiary);
        }

        chairRoom = chairRoomRepository.save(chairRoom);
        return mapper.toResponse(chairRoom);
    }

    @Transactional
    public void deleteChairRoom(UUID id) {
        chairRoomRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ChairRoomResponse> findAvailableRoomsForTimeSlot(
            UUID subsidiaryId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        return chairRoomRepository.findAvailableRoomsForTimeSlot(subsidiaryId, startTime, endTime)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
