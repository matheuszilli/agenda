package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import com.agenda.app.model.Company;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    /* ───────────── de DTO → Entity ──────────── */
    @Mapping(target = "id", ignore = true)
    Company toEntity(CompanyRequest dto);

    /* ───────────── de Entity → DTO ──────────── */
    CompanyResponse toResponse(Company entity);

    /* PUT ↔ atualizar entity existente */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CompanyRequest dto, @MappingTarget Company entity);

    List<CompanyResponse> toResponseList(List<Company> entities);
}
