package com.agenda.app.service;

import com.agenda.app.dto.ChairRoomResponse;
import com.agenda.app.dto.ChairRoomScheduleEntryRequest;
import com.agenda.app.dto.ChairRoomScheduleEntryResponse;
import com.agenda.app.exception.ScheduleConflictException;
import com.agenda.app.mapper.ChairRoomMapper;
import com.agenda.app.mapper.ChairRoomScheduleEntryMapper;
import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.ChairRoomScheduleEntry;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.repository.ChairRoomRepository;
import com.agenda.app.repository.ChairRoomScheduleEntryRepository;
import com.agenda.app.repository.SubsidiaryScheduleEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChairRoomScheduleService {

    private final ChairRoomRepository chairRoomRepository;
    private final ChairRoomScheduleEntryRepository scheduleRepository;
    private final SubsidiaryScheduleEntryRepository subsidiaryScheduleRepository;
    private final ChairRoomScheduleEntryMapper mapper;
    private final ChairRoomMapper chairRoomMapper;

    /**
     * Cria um novo agendamento para uma sala/cadeira
     */
    @Transactional
    public ChairRoomScheduleEntryResponse create(ChairRoomScheduleEntryRequest request) {
        // Buscar a sala/cadeira
        ChairRoom chairRoom = chairRoomRepository.findById(request.getChairRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + request.getChairRoomId()));

        // Verificar se já existe agendamento para esta data
        if (scheduleRepository.existsByChairRoomIdAndDate(request.getChairRoomId(), request.getDate())) {
            throw new IllegalArgumentException("Schedule already exists for this date");
        }

        // Verificar se o horário da sala está dentro do horário da subsidiária
        validateSubsidiaryHours(chairRoom, request.getDate(), request.getOpenTime(), request.getCloseTime());

        // Criar e salvar o agendamento
        ChairRoomScheduleEntry entity = mapper.toEntity(request, chairRoom);
        entity.setCustomized(true);  // Marcamos como customizado pois é um agendamento específico
        scheduleRepository.save(entity);

        return mapper.toResponse(entity);
    }

    /**
     * Cria ou atualiza um agendamento para uma sala/cadeira em uma data específica
     *
     * @param chairRoomId      ID da sala/cadeira
     * @param date             Data do agendamento
     * @param openTime         Horário de abertura
     * @param closeTime        Horário de fechamento
     * @param closed           Se a sala/cadeira está fechada neste dia
     * @param replaceExisting  Se deve substituir um agendamento existente
     * @return O agendamento criado/atualizado
     */
    @Transactional
    public ChairRoomScheduleEntryResponse createOrUpdateSchedule(
            UUID chairRoomId,
            LocalDate date,
            LocalTime openTime,
            LocalTime closeTime,
            boolean closed,
            boolean replaceExisting) {

        ChairRoom chairRoom = chairRoomRepository.findById(chairRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + chairRoomId));

        // Verificar se o horário da sala está dentro do horário da subsidiária
        if (!closed) { // Não precisa validar horários se estiver fechado
            validateSubsidiaryHours(chairRoom, date, openTime, closeTime);
        }

        // Verificar se já existe um agendamento para esta data
        Optional<ChairRoomScheduleEntry> existingOpt = scheduleRepository.findByChairRoomIdAndDate(chairRoomId, date);

        if (existingOpt.isPresent()) {
            if (!replaceExisting) {
                List<LocalDate> conflictDates = new ArrayList<>();
                conflictDates.add(date);
                throw new ScheduleConflictException(
                        "Schedule already exists for this date. Use replaceExisting=true to override.",
                        chairRoomId,
                        conflictDates);
            }

            // Atualizar o agendamento existente
            ChairRoomScheduleEntry existing = existingOpt.get();
            existing.setOpenTime(openTime);
            existing.setCloseTime(closeTime);
            existing.setClosed(closed);
            existing.setCustomized(true);
            scheduleRepository.save(existing);
            return mapper.toResponse(existing);
        } else {
            // Criar um novo agendamento
            ChairRoomScheduleEntry entry = new ChairRoomScheduleEntry();
            entry.setChairRoom(chairRoom);
            entry.setDate(date);
            entry.setOpenTime(openTime);
            entry.setCloseTime(closeTime);
            entry.setClosed(closed);
            entry.setCustomized(true);
            scheduleRepository.save(entry);
            return mapper.toResponse(entry);
        }
    }

    /**
     * Validação de horários da subsidiária
     */
    private void validateSubsidiaryHours(ChairRoom chairRoom, LocalDate date, LocalTime openTime, LocalTime closeTime) {
        Subsidiary subsidiary = chairRoom.getSubsidiary();
        var subsidiaryScheduleOpt = subsidiaryScheduleRepository
                .findBySubsidiaryIdAndDate(subsidiary.getId(), date)
                .stream()
                .findFirst();

        if (subsidiaryScheduleOpt.isPresent()) {
            var subsidiarySchedule = subsidiaryScheduleOpt.get();

            // Verificar se a subsidiária está fechada neste dia
            if (subsidiarySchedule.isClosed()) {
                throw new IllegalArgumentException("Cannot schedule chair/room when subsidiary is closed");
            }

            // Verificar se o horário da sala está dentro do horário da subsidiária
            if (openTime.isBefore(subsidiarySchedule.getOpenTime()) ||
                    closeTime.isAfter(subsidiarySchedule.getCloseTime())) {
                throw new IllegalArgumentException(
                        "Chair/Room schedule must be within subsidiary hours: " +
                                subsidiarySchedule.getOpenTime() + " - " + subsidiarySchedule.getCloseTime());
            }
        }
    }

    /**
     * Busca todos os agendamentos com paginação
     */
    @Transactional(readOnly = true)
    public Page<ChairRoomScheduleEntryResponse> getAllChairRoomSchedules(Pageable pageable) {
        return scheduleRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * Busca um agendamento específico por ID
     */
    @Transactional(readOnly = true)
    public ChairRoomScheduleEntryResponse getChairRoomScheduleById(UUID id) {
        return scheduleRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Chair room schedule not found with id: " + id));
    }

    /**
     * Busca todos os agendamentos para uma sala/cadeira
     */
    @Transactional(readOnly = true)
    public List<ChairRoomScheduleEntryResponse> getByChairRoomId(UUID chairRoomId) {
        return scheduleRepository.findByChairRoomId(chairRoomId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca agendamento para uma sala/cadeira em uma data específica
     */
    @Transactional(readOnly = true)
    public ChairRoomScheduleEntryResponse getByChairRoomAndDate(UUID chairRoomId, LocalDate date) {
        return scheduleRepository.findByChairRoomIdAndDate(chairRoomId, date)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("No schedule found for this chair/room on " + date));
    }

    /**
     * Atualiza um agendamento existente
     */
    @Transactional
    public ChairRoomScheduleEntryResponse update(UUID id, ChairRoomScheduleEntryRequest request) {
        ChairRoomScheduleEntry entry = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found: " + id));

        // Verificar se o horário da sala está dentro do horário da subsidiária
        if (!request.isClosed()) {
            validateSubsidiaryHours(
                    entry.getChairRoom(), 
                    request.getDate(), 
                    request.getOpenTime(), 
                    request.getCloseTime());
        }

        mapper.updateFromRequest(request, entry);
        entry.setCustomized(true);
        scheduleRepository.save(entry);

        return mapper.toResponse(entry);
    }

    /**
     * Remove um agendamento
     */
    @Transactional
    public void delete(UUID id) {
        scheduleRepository.deleteById(id);
    }

    /**
     * Encontra salas disponíveis para um determinado período
     */
    @Transactional(readOnly = true)
    public List<ChairRoomResponse> findAvailableRoomsForTimeSlot(
            UUID subsidiaryId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        return chairRoomRepository.findAvailableRoomsForTimeSlot(subsidiaryId, startTime, endTime)
                .stream()
                .map(chairRoomMapper::toResponse)
                .toList();
    }
}