package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import com.agenda.app.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProfessionalScheduleEntryMapper {

    @Mapping(source = "professional.id", target = "professionalId")
    ProfessionalScheduleEntryResponse toResponse(ProfessionalScheduleEntry entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professional", expression = "java(professional)")
    ProfessionalScheduleEntry toEntity(ProfessionalScheduleEntryRequest dto, @Context Professional professional);

}