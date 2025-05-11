// src/main/java/com/agenda/app/mapper/BusinessServiceMapper.java
package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import com.agenda.app.model.BusinessService;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BusinessServiceMapper {

    @Mapping(source = "company.id", target = "companyId")
    BusinessServiceResponse toResponse(BusinessService entity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true) // id é gerado
    BusinessService toEntity(BusinessServiceRequest dto);
}
