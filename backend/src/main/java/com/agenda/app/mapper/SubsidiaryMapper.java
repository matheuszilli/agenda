package com.agenda.app.mapper;

import com.agenda.app.dto.SubsidiaryRequest;
import com.agenda.app.dto.SubsidiaryResponse;
import com.agenda.app.model.Company;
import com.agenda.app.model.Subsidiary;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface SubsidiaryMapper {

    /* entity -> response */
    @Mapping(source = "company.id", target = "companyId")
    SubsidiaryResponse toResponse(Subsidiary entity);
    
    /* request -> entity (create) */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.name",           target = "name")
    @Mapping(source = "dto.address",        target = "address")
    @Mapping(source = "dto.documentNumber", target = "documentNumber")
    @Mapping(source = "dto.openTime",       target = "openTime")
    @Mapping(source = "dto.closeTime",      target = "closeTime")
    @Mapping(target = "company", expression = "java(company)")
    @Mapping(target = "scheduleEntries", ignore = true)
    Subsidiary toEntity(SubsidiaryRequest dto, Company company);

    /* merge (update) – dto tem todos campos opcionais */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "scheduleEntries", ignore = true)
    void copyNonNullToEntity(SubsidiaryRequest dto, @MappingTarget Subsidiary entity);
}