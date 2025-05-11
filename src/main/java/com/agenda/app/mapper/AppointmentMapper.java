package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import com.agenda.app.model.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "professional.id",    target = "professionalId")
    @Mapping(source = "customer.id",        target = "customerId")
    @Mapping(source = "subsidiary.id",      target = "subsidiaryId")
    @Mapping(source = "businessService.id", target = "serviceId")
    @Mapping(source = "company.id",         target = "companyId")
    @Mapping(source = "startTime",          target = "startTime")
    @Mapping(source = "endTime",            target = "endTime")
    @Mapping(source = "status",             target = "status")
    AppointmentResponse toResponse(Appointment entity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    Appointment toEntity(AppointmentRequest dto);
}
