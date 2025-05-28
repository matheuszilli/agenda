package com.agenda.app.mapper;

import com.agenda.app.dto.CompanyRequest;
import com.agenda.app.dto.CompanyResponse;
import com.agenda.app.model.Company;
import com.agenda.app.util.CnpjUtils;
import com.agenda.app.util.PhoneUtils;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { AddressMapper.class } // mapeia AddressRequest ↔ Address e Address ↔ AddressResponse
)
public interface CompanyMapper {

    @Named("formatCnpj")
    default String formatCnpj(String raw) {
        return CnpjUtils.formatCnpj(raw);
    }

    @Named("formatPhone")
    default String formatPhone(String raw) {
        return (raw == null || raw.isBlank())
            ? null
            : PhoneUtils.formatPhone(raw);
    }

    // ------ DTO → Entidade (create) ------
    @Mapping(source = "dto.documentNumber", target = "documentNumber", qualifiedByName = "formatCnpj")
    @Mapping(source = "dto.phone",          target = "phone",          qualifiedByName = "formatPhone")
    Company toEntity(CompanyRequest dto);

    // ------ DTO → Entidade (update) ------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dto.documentNumber", target = "documentNumber", qualifiedByName = "formatCnpj")
    @Mapping(source = "dto.phone",          target = "phone",          qualifiedByName = "formatPhone")
    void updateEntityFromDto(CompanyRequest dto, @MappingTarget Company entity);

    // ------ Entidade → Response ------
    CompanyResponse toResponse(Company entity);

    // ------ Lista de entidades → Lista de responses ------
    List<CompanyResponse> toResponseList(List<Company> entities);
}