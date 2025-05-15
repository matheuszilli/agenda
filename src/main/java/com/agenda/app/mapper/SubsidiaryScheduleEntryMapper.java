package com.agenda.app.mapper;

import com.agenda.app.dto.SubsidiaryScheduleEntryRequest;
import com.agenda.app.dto.SubsidiaryScheduleEntryResponse;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.model.SubsidiaryScheduleEntry;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubsidiaryScheduleEntryMapper {

    /* Request → Entity (usado para CREATE) */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subsidiary", expression = "java(subsidiary)")
    SubsidiaryScheduleEntry toEntity(SubsidiaryScheduleEntryRequest request,
                                     @Context Subsidiary subsidiary);

    /* Entity → Response */
    @Mapping(source = "subsidiary.id", target = "subsidiaryId")
    SubsidiaryScheduleEntryResponse toResponse(SubsidiaryScheduleEntry entity);

    /* Atualização parcial (usado em PUT/PATCH) */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(SubsidiaryScheduleEntryRequest request,
                           @MappingTarget SubsidiaryScheduleEntry entity);
}