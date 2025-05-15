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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository     appointmentRepository;
    private final ProfessionalRepository    professionalRepository;
    private final CustomerRepository        customerRepository;
    private final SubsidiaryRepository      subsidiaryRepository;
    private final ItemRepository itemRepository;
    private final PaymentService            paymentService;
    private final AppointmentMapper         mapper;

    @Transactional
    public AppointmentResponse scheduleAppointment(AppointmentRequest dto) {
        Professional prof = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + dto.getProfessionalId()));
        Customer cust = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + dto.getCustomerId()));
        Subsidiary sub = subsidiaryRepository.findById(dto.getSubsidiaryId())
                .orElseThrow(() -> new EntityNotFoundException("Subsidiary not found: " + dto.getSubsidiaryId()));
        Item svc = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found: " + dto.getItemId()));

        LocalDateTime start = dto.getStartTime();
        if (start.toLocalDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Start must be today or later");
        LocalDateTime end = dto.getEndTime();

        if (appointmentRepository.existsOverlapInOtherSubsidiary(
                prof.getId(), sub.getId(), start, end)) {
            throw new ConflictException("Professional has conflict in another subsidiary");
        }

        Appointment appt = new Appointment();
        appt.setProfessional(prof);
        appt.setCustomer(cust);
        appt.setSubsidiary(sub);
        appt.setItem(svc);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setCompany(sub.getCompany());
        appt.setNotes(dto.getNotes());
        appt.setStatus(determineStatus(appt, dto.getPaymentId()));

        appointmentRepository.save(appt);
        return mapper.toResponse(appt);
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

        Professional prof = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new EntityNotFoundException("Professional not found: " + dto.getProfessionalId()));
        Customer cust = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + dto.getCustomerId()));
        Subsidiary sub = subsidiaryRepository.findById(dto.getSubsidiaryId())
                .orElseThrow(() -> new EntityNotFoundException("Subsidiary not found: " + dto.getSubsidiaryId()));
        Item svc = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found: " + dto.getItemId()));

        LocalDateTime start = dto.getStartTime();
        if (start.toLocalDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Start must be today or later");
        LocalDateTime end = dto.getEndTime();

        if (appointmentRepository.existsOverlapInOtherSubsidiary(
                prof.getId(), sub.getId(), start, end)) {
            throw new ConflictException("Professional has conflict in another subsidiary");
        }

        appt.setProfessional(prof);
        appt.setCustomer(cust);
        appt.setSubsidiary(sub);
        appt.setItem(svc);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setNotes(dto.getNotes());
        appt.setCompany(sub.getCompany());
        appt.setStatus(determineStatus(appt, dto.getPaymentId()));

        appointmentRepository.save(appt);
        return mapper.toResponse(appt);
    }

    private AppointmentStatus determineStatus(Appointment appt, /* pode ser null */ UUID paymentId) {

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
}