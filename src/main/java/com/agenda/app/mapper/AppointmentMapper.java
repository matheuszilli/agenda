package com.agenda.app.mapper;

import com.agenda.app.dto.AppointmentResponse;
import com.agenda.app.model.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "professional.id",    target = "professionalId")
    @Mapping(source = "customer.id",        target = "customerId")
    @Mapping(source = "subsidiary.id",      target = "subsidiaryId")
    @Mapping(source = "businessService.id", target = "serviceId")
    AppointmentResponse toResponse(Appointment entity);
}