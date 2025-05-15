package com.agenda.app.mapper;

import com.agenda.app.dto.ProfessionalServiceCfgRequest;
import com.agenda.app.dto.ProfessionalServiceCfgResponse;
import com.agenda.app.model.ProfessionalServiceCfg;
import com.agenda.app.repository.ItemRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProfessionalServiceCfgMapper {

    /* ---------- request -> entity ---------- */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "professional", ignore = true)  // setamos à parte
    @Mapping(target = "service",
            expression = "java(serviceRepo.findById(dto.serviceId())" +
                    ".orElseThrow(() -> new IllegalArgumentException(\"Service not found\")))")
    ProfessionalServiceCfg toEntity(ProfessionalServiceCfgRequest dto,
                                    @Context ItemRepository serviceRepo);

    /* ---------- entity -> response ---------- */
    @Mapping(source = "professional.id", target = "professionalId")
    @Mapping(source = "service.id",       target = "serviceId")
    @Mapping(target = "priceEffective",
            expression = "java(entity.getCustomPrice()    != null ? entity.getCustomPrice()    : entity.getService().getPrice())")
    @Mapping(target = "durationEffective",
            expression = "java(entity.getCustomDurationMinutes() != null ? entity.getCustomDurationMinutes() : entity.getService().getDurationMinutes())")
    ProfessionalServiceCfgResponse toResponse(ProfessionalServiceCfg entity);
}
