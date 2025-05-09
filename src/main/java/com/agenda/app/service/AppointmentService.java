package com.agenda.app.service;

import com.agenda.app.dto.AppointmentRequest;
import com.agenda.app.dto.AppointmentResponse;
import com.agenda.app.exception.ConflictException;
import com.agenda.app.exception.PaymentRequiredException;
import com.agenda.app.mapper.AppointmentMapper;
import com.agenda.app.model.*;
import com.agenda.app.repository.*;
import lombok.RequiredArgsConstructor;
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
    private final BusinessServiceRepository businessServiceRepository;
    private final PaymentService            paymentService;
    private final AppointmentMapper         mapper;

    /** Faz o agendamento ✅ devolvendo DTO de saída */
    @Transactional
    public AppointmentResponse scheduleAppointment(AppointmentRequest dto) {

        Professional prof = professionalRepository.findById(dto.professionalId())
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));
        Customer cust = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Subsidiary sub = subsidiaryRepository.findById(dto.subsidiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Subsidiary not found"));
        BusinessService svc = businessServiceRepository.findById(dto.serviceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        /* ---------- validações simples de data ---------- */
        LocalDateTime start = dto.startTime();
        if (start.toLocalDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Start must be today or later");

        LocalDateTime end = start.plusMinutes(svc.getDurationMinutes());

        /* ---------- conflito de profissional em filial diferente ---------- */
        if (appointmentRepository.existsOverlapInOtherSubsidiary(
                prof.getId(), sub.getId(), start, end)) {
            throw new ConflictException("Profissional já tem horário nesse período em outra filial");
        }

        /* ---------- monta entidade ---------- */
        Appointment appt = new Appointment();
        appt.setProfessional(prof);
        appt.setCustomer(cust);
        appt.setSubsidiary(sub);
        appt.setBusinessService(svc);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setCompany(sub.getCompany());

        /* ---------- status / pré-pagamento (≤2 dias) ---------- */
        appt.setStatus(determineStatus(appt, dto.paymentId()));

        appointmentRepository.save(appt);
        return mapper.toResponse(appt);          // <<== devolve DTO de saída
    }

    private AppointmentStatus determineStatus(Appointment appt, /* pode ser null */ UUID paymentId) {

        BusinessService bs = appt.getBusinessService();

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