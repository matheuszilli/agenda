package com.agenda.app.service;

import com.agenda.app.dto.AppointmentRequest;
import com.agenda.app.dto.AppointmentResponse;
import com.agenda.app.exception.ConflictException;
import com.agenda.app.exception.PaymentRequiredException;
import com.agenda.app.mapper.AppointmentMapper;
import com.agenda.app.model.*;
import com.agenda.app.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final CustomerRepository customerRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final ChairRoomRepository chairRoomRepository;
    private final ItemRepository itemRepository;
    private final PaymentService paymentService;
    private final AppointmentMapper mapper;

    private final SubsidiaryScheduleEntryRepository subsidiaryScheduleRepository;
    private final ProfessionalScheduleEntryRepository professionalScheduleRepository;
    private final ChairRoomScheduleEntryRepository chairRoomScheduleRepository;
    private final ProfessionalChairRoomAssignmentRepository professionalChairRoomAssignmentRepository;

    @Transactional
    public AppointmentResponse scheduleAppointment(AppointmentRequest dto) {
        // Buscar entidades relacionadas
        Professional prof = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + dto.getProfessionalId()));

        Customer cust = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + dto.getCustomerId()));

        Subsidiary sub = subsidiaryRepository.findById(dto.getSubsidiaryId())
                .orElseThrow(() -> new EntityNotFoundException("Subsidiary not found: " + dto.getSubsidiaryId()));

        Item svc = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found: " + dto.getItemId()));

        // Verificar a data/hora do agendamento
        LocalDateTime start = dto.getStartTime();
        if (start.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date must be today or later");
        }

        LocalDateTime end = dto.getEndTime();
        LocalDate appointmentDate = start.toLocalDate();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        // Verificar disponibilidade da Subsidiária
        verifySubsidiaryAvailability(sub.getId(), appointmentDate, startTime, endTime);

        // Verificar disponibilidade do Profissional
        verifyProfessionalAvailability(prof.getId(), appointmentDate, startTime, endTime);

        // Verificar conflitos com outros agendamentos do profissional
        if (appointmentRepository.existsOverlapInOtherSubsidiary(
                prof.getId(), sub.getId(), start, end)) {
            throw new ConflictException("Professional has conflict in another subsidiary");
        }

        // Verificar disponibilidade da Sala/Cadeira se especificada
        ChairRoom chairRoom = null;
        if (dto.getChairRoomId() != null) {
            chairRoom = chairRoomRepository.findById(UUID.fromString(dto.getChairRoomId()))
                    .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + dto.getChairRoomId()));

            verifyChairRoomAvailability(chairRoom.getId(), appointmentDate, startTime, endTime);
            
            // Verificar se o profissional está atribuído a esta cadeira/sala no horário
            verifyProfessionalChairRoomAssignment(prof.getId(), chairRoom.getId(), appointmentDate, startTime, endTime);
        } else {
            // Verificar se o profissional precisa estar em uma cadeira/sala específica
            verifyProfessionalRequiresChairRoom(prof.getId(), appointmentDate);
        }

        // Criar o agendamento
        Appointment appt = new Appointment();
        appt.setProfessional(prof);
        appt.setCustomer(cust);
        appt.setSubsidiary(sub);
        appt.setItem(svc);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setCompany(sub.getCompany());
        appt.setNotes(dto.getNotes());
        appt.setChairRoom(chairRoom);
        appt.setStatus(determineStatus(appt, dto.getPaymentId()));

        appointmentRepository.save(appt);
        return mapper.toResponse(appt);
    }

    /**
     * Verifica se a subsidiária está disponível na data e horário especificados
     */
    private void verifySubsidiaryAvailability(UUID subsidiaryId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Buscar horário da subsidiária para a data
        Optional<SubsidiaryScheduleEntry> scheduleOpt = subsidiaryScheduleRepository
                .findBySubsidiaryIdAndDate(subsidiaryId, date)
                .stream()
                .findFirst();

        // Se não encontrar horário específico, a subsidiária está fechada
        if (scheduleOpt.isEmpty()) {
            throw new ConflictException("Subsidiary is not available on " + date);
        }

        SubsidiaryScheduleEntry schedule = scheduleOpt.get();

        // Verificar se a subsidiária está fechada neste dia
        if (schedule.isClosed()) {
            throw new ConflictException("Subsidiary is closed on " + date);
        }

        // Verificar se o horário do agendamento está dentro do horário da subsidiária
        if (startTime.isBefore(schedule.getOpenTime()) || endTime.isAfter(schedule.getCloseTime())) {
            throw new ConflictException(
                    "Appointment must be within subsidiary hours: " +
                            schedule.getOpenTime() + " - " + schedule.getCloseTime());
        }
    }

    /**
     * Verifica se o profissional está disponível na data e horário especificados
     */
    private void verifyProfessionalAvailability(UUID professionalId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        var scheduleOpt = professionalScheduleRepository.findFirstByProfessionalIdAndDate(professionalId, date);

        if (scheduleOpt.isEmpty()) {
            throw new ConflictException("Professional is not available on " + date);
        }

        var schedule = scheduleOpt.get();

        if (startTime.isBefore(schedule.getStartTime()) || endTime.isAfter(schedule.getEndTime())) {
            throw new ConflictException("Professional is not available at this time");
        }
        
        // Verificar conflitos com outros agendamentos do profissional
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);
        
        boolean hasConflict = appointmentRepository.findByProfessionalIdAndStartTimeAfterAndEndTimeBefore(
                professionalId, start.minusDays(1), end.plusDays(1))
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(a -> {
                    // Verificar se há sobreposição de horários
                    return (a.getStartTime().isBefore(end) && a.getEndTime().isAfter(start));
                });
        
        if (hasConflict) {
            throw new ConflictException("Professional already has an appointment at this time");
        }
    }
    
    /**
     * Verifica se o profissional está atribuído à cadeira/sala no horário
     */
    private void verifyProfessionalChairRoomAssignment(
            UUID professionalId, 
            UUID chairRoomId, 
            LocalDate date, 
            LocalTime startTime, 
            LocalTime endTime) {
        
        // Buscar atribuições específicas para a data
        var assignments = professionalChairRoomAssignmentRepository
                .findByProfessionalIdAndChairRoomIdAndDate(professionalId, chairRoomId, date);
        
        // Se não encontrar atribuição específica, buscar atribuição recorrente
        if (assignments.isEmpty()) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            assignments = professionalChairRoomAssignmentRepository
                    .findByProfessional_IdAndChairRoom_IdAndRecurringTrueAndDayOfWeek(
                            professionalId, chairRoomId, dayOfWeek);
        }
        
        // Se não encontrar nenhuma atribuição, verificar se é necessária
        if (assignments.isEmpty()) {
            // Verificar se o profissional está atribuído a outra cadeira/sala no mesmo dia
            var otherAssignments = professionalChairRoomAssignmentRepository.findEffectiveAssignmentsForDate(
                    professionalId, date);
            
            if (!otherAssignments.isEmpty()) {
                throw new ConflictException(
                        "Professional is assigned to another chair/room on this date");
            }
            
            // Se não estiver atribuído a nenhuma cadeira/sala, pode usar qualquer uma
            return;
        }
        
        // Verificar se alguma atribuição cobre o horário do agendamento
        boolean isAssigned = assignments.stream()
                .anyMatch(assignment -> {
                    return !endTime.isBefore(assignment.getStartTime()) && 
                           !startTime.isAfter(assignment.getEndTime());
                });
        
        if (!isAssigned) {
            throw new ConflictException(
                    "Professional is not assigned to this chair/room at the requested time");
        }
    }
    
    /**
     * Verifica se o profissional precisa estar em uma cadeira/sala específica
     */
    private void verifyProfessionalRequiresChairRoom(UUID professionalId, LocalDate date) {
        // Verificar se o profissional está atribuído a alguma cadeira/sala neste dia
        boolean hasAssignment = professionalChairRoomAssignmentRepository.existsByProfessional_IdAndDate(
                professionalId, date);
        
        // Se não encontrar atribuição específica, verificar atribuições recorrentes
        if (!hasAssignment) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            var recurringAssignments = professionalChairRoomAssignmentRepository
                    .findByChairRoom_IdAndRecurringTrueAndDayOfWeek(professionalId, dayOfWeek);
            
            hasAssignment = !recurringAssignments.isEmpty();
        }
        
        if (hasAssignment) {
            throw new ConflictException(
                    "Professional requires a specific chair/room for this date");
        }
    }


    /**
     * Verifica se a sala/cadeira está disponível na data e horário especificados
     */
    private void verifyChairRoomAvailability(UUID chairRoomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Buscar horário da sala/cadeira para a data
        Optional<ChairRoomScheduleEntry> scheduleOpt = chairRoomScheduleRepository
                .findByChairRoomIdAndDate(chairRoomId, date);

        // Se não encontrar horário específico, a sala/cadeira não está disponível
        if (scheduleOpt.isEmpty()) {
            throw new ConflictException("Chair/Room is not available on " + date);
        }

        ChairRoomScheduleEntry schedule = scheduleOpt.get();

        // Verificar se a sala/cadeira está fechada neste dia
        if (schedule.isClosed()) {
            throw new ConflictException("Chair/Room is closed on " + date);
        }

        // Verificar se o horário do agendamento está dentro do horário da sala/cadeira
        if (startTime.isBefore(schedule.getOpenTime()) || endTime.isAfter(schedule.getCloseTime())) {
            throw new ConflictException(
                    "Appointment must be within chair/room hours: " +
                            schedule.getOpenTime() + " - " + schedule.getCloseTime());
        }

        // Verificar se a sala/cadeira já está em uso no horário
        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = LocalDateTime.of(date, endTime);

        boolean hasConflict = appointmentRepository.findAll().stream()
                .filter(a -> a.getChairRoom() != null && a.getChairRoom().getId().equals(chairRoomId))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(a -> {
                    // Verificar se há sobreposição de horários
                    return (a.getStartTime().isBefore(end) && a.getEndTime().isAfter(start));
                });

        if (hasConflict) {
            throw new ConflictException("Chair/Room is already booked for this time");
        }
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> listAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + id));
    }

    @Transactional
    public AppointmentResponse updateAppointment(UUID id, AppointmentRequest dto) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + id));

        // Validar e buscar entidades relacionadas
        Professional prof = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + dto.getProfessionalId()));

        Customer cust = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + dto.getCustomerId()));

        Subsidiary sub = subsidiaryRepository.findById(dto.getSubsidiaryId())
                .orElseThrow(() -> new EntityNotFoundException("Subsidiary not found: " + dto.getSubsidiaryId()));

        Item svc = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found: " + dto.getItemId()));

        // Validar data/hora
        LocalDateTime start = dto.getStartTime();
        if (start.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date must be today or later");
        }

        LocalDateTime end = dto.getEndTime();
        LocalDate appointmentDate = start.toLocalDate();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        // Verificar disponibilidade
        verifySubsidiaryAvailability(sub.getId(), appointmentDate, startTime, endTime);
        verifyProfessionalAvailability(prof.getId(), appointmentDate, startTime, endTime);

        if (appointmentRepository.existsOverlapInOtherSubsidiary(prof.getId(), sub.getId(), start, end)) {
            throw new ConflictException("Professional has a conflict in another subsidiary");
        }

        // Verificar sala/cadeira
        ChairRoom chairRoom = null;
        if (dto.getChairRoomId() != null) {
            chairRoom = chairRoomRepository.findById(UUID.fromString(dto.getChairRoomId()))
                    .orElseThrow(() -> new EntityNotFoundException("Chair/Room not found: " + dto.getChairRoomId()));

            verifyChairRoomAvailability(chairRoom.getId(), appointmentDate, startTime, endTime);
        }

        // Atualizar o agendamento
        appt.setProfessional(prof);
        appt.setCustomer(cust);
        appt.setSubsidiary(sub);
        appt.setItem(svc);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setNotes(dto.getNotes());
        appt.setChairRoom(chairRoom);
        appt.setCompany(sub.getCompany());
        appt.setStatus(determineStatus(appt, dto.getPaymentId()));

        appointmentRepository.save(appt);
        return mapper.toResponse(appt);
    }

    private AppointmentStatus determineStatus(Appointment appt, UUID paymentId) {
        Item bs = appt.getItem();

        try {
            Payment p = paymentService.verifyPrePaymentIfWithinWindow(
                    bs,
                    appt.getStartTime(),
                    paymentId);
            if (p != null);
        } catch (PaymentRequiredException e) {
            return AppointmentStatus.PENDING;
        }

        // Confirmado? (ex.: recepcionista já marcou)
        boolean confirmed = appt.getStatus() == AppointmentStatus.CONFIRMED;

        if (bs.isRequiresPrePayment()) {
            return confirmed ? AppointmentStatus.CONFIRMED : AppointmentStatus.NOT_CONFIRMED;
        } else {
            return confirmed ? AppointmentStatus.CONFIRMED : AppointmentStatus.NOT_CONFIRMED;
        }
    }

    @Transactional
    public void cancelAppointment(UUID id) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + id));
        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }

    public Page<AppointmentResponse> getAgendaAppointments(Pageable pageable) {
        List<AppointmentStatus> visibleStatuses = List.of(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.NOT_CONFIRMED,
                AppointmentStatus.PENDING
        );

        return appointmentRepository.findByStatusIn(visibleStatuses, pageable)
                .map(mapper::toResponse);
    }
}