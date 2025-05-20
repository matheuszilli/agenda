package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import org.mapstruct.*;
import com.agenda.app.model.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    @Mapping(source = "professional.id", target = "professionalId")
    @Mapping(source = "customer.id",     target = "customerId")
    @Mapping(source = "subsidiary.id",   target = "subsidiaryId")
    @Mapping(source = "item.id",         target = "itemId")   // << aqui
    @Mapping(source = "company.id",      target = "companyId")
    AppointmentResponse toResponse(Appointment entity);

    @InheritInverseConfiguration
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "status",      ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    Appointment toEntity(AppointmentRequest dto);
}