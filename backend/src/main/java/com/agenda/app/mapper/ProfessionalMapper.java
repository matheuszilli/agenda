// src/main/java/com/agenda/app/mapper/ProfessionalMapper.java
package com.agenda.app.mapper;

import com.agenda.app.dto.CustomerRequest;
import com.agenda.app.dto.CustomerResponse;
import com.agenda.app.dto.ProfessionalRequest;
import com.agenda.app.dto.ProfessionalResponse;
import com.agenda.app.model.Company;
import com.agenda.app.model.Customer;
import com.agenda.app.model.Professional;
import com.agenda.app.model.Subsidiary;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {AddressMapper.class}
)
public interface ProfessionalMapper {

    /* ---------- entity → response ---------- */
    @Mapping(source = "subsidiary.id", target = "subsidiaryId")
    ProfessionalResponse toResponse(Professional entity);

    /* ---------- request → entity (create) ---------- */
    // --> todos os campos explícitos vêm do DTO
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "fullName",       ignore = true)   // gerado pelo @PrePersist
    @Mapping(target = "user",           ignore = true)
    @Mapping(target = "serviceConfigs", ignore = true)
    @Mapping(target = "subsidiary",     expression = "java(subs)")
    @Mapping(source = "firstName",           target = "firstName")
    @Mapping(source = "lastName",            target = "lastName")
    @Mapping(source = "documentNumber",      target = "documentNumber")
    @Mapping(source = "address",             target = "address")
    @Mapping(source = "phone",               target = "phone")
    @Mapping(source = "email",               target = "email")
    Professional toEntity(ProfessionalRequest req,
                          @Context Subsidiary   subs);

    /* ---------- merge (update) ---------- */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "subsidiary", ignore = true)
    @Mapping(target = "user",       ignore = true)
    void copyNonNullToEntity(ProfessionalRequest req,
                             @MappingTarget Professional entity);

}