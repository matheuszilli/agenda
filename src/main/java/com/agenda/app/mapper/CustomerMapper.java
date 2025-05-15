package com.agenda.app.mapper;

import com.agenda.app.dto.CustomerRequest;
import com.agenda.app.dto.CustomerResponse;
import com.agenda.app.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // entity → response
    @Mapping(source = "company.id", target = "companyId")
    CustomerResponse toResponse(Customer entity);

    // request → entity, a service vai buscar a Company e passar como @Context
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName",  target = "lastName")
    @Mapping(source = "email",     target = "email")
    @Mapping(source = "phone",     target = "phone")
    @Mapping(source = "documentNumber", target = "documentNumber")
    @Mapping(source = "address",   target = "address")
    @Mapping(target = "company",   expression = "java(company)")
    Customer toEntity(CustomerRequest req, @Context Company company);

    // atualização parcial
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(CustomerRequest req, @MappingTarget Customer entity);
}
