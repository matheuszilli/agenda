package com.agenda.app.service;

import com.agenda.app.model.*;
import com.agenda.app.repository.AppointmentRepository;
import com.agenda.app.repository.ProfessionalRepository;
import com.agenda.app.repository.SubsidiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SubsidiaryRepository subsidiaryRepository;
    private final ProfessionalRepository professionalRepository;

    public Appointment scheduleAppointment(Appointment appointment){

        require(appointment.getProfessional(), "Professional is required");
        require(appointment.getBusinessService(), "Service is required");
        require(appointment.getCustomer(), "Customer is required");
        require(appointment.getStartTime(), "Start time is required");
        require(appointment.getEndTime(), "End time is required");
        require(appointment.getSubsidiary(), "Subsidiary is required");
        require(appointment.getCompany(), "Company is required");


        if(appointment.getStartTime().toLocalDate().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Start must be today or later");
        }

        if(appointment.getStartTime().toLocalTime().isBefore(appointment.getProfessional().getAvailableStart())) {
            throw new IllegalArgumentException("Start time is before professional's available start time");
        }

        if(appointment.getStartTime().toLocalTime().isAfter(appointment.getProfessional().getAvailableEnd())) {
            throw new IllegalArgumentException("Start time is after professional's available end time");
        }

        if (appointment.getStartTime().toLocalTime().isBefore(appointment.getSubsidiary().getOpenTime())) {
            throw new IllegalArgumentException("Start time is before subsidiary's open time");
        }

        if (appointment.getStartTime().toLocalTime().isAfter(appointment.getSubsidiary().getCloseTime())) {
            throw new IllegalArgumentException("Start time is after subsidiary's close time");
        }


        appointment.setStatus(determineStatus(appointment));
        return appointmentRepository.save(appointment);
    }

    private void require(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private AppointmentStatus determineStatus(Appointment appointment) {
        boolean requiresPrePayment = appointment.getBusinessService().isRequiresPrePayment();

        Payment payment = appointment.getServiceOrder() != null ? appointment.getServiceOrder().getPayment() : null;

        boolean hasPaid = payment != null &&
                payment.getStatus() == PaymentStatus.COMPLETED &&
                payment.getAmount().compareTo(BigDecimal.ZERO) > 0;

        AppointmentStatus current = appointment.getStatus();
        boolean hasConfirmedAppointment = current != null && current == AppointmentStatus.CONFIRMED;

        if (requiresPrePayment) {
            if (!hasPaid) {
                return AppointmentStatus.PENDING;
            }
            if (!hasConfirmedAppointment) {
                return AppointmentStatus.NOT_CONFIRMED;
            }
            return AppointmentStatus.CONFIRMED;
        } else {
            if (hasConfirmedAppointment) {
                return AppointmentStatus.CONFIRMED;
            } else {
                return AppointmentStatus.NOT_CONFIRMED;
            }
        }
    }
}
