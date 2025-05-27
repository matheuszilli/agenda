package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import com.agenda.app.model.Item;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "subsidiary.id", target = "subsidiaryId")
    @Mapping(source = "active", target = "active")
    ItemResponse toResponse(Item entity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true) // id é gerado
    @Mapping(target = "company", ignore = true) // será setado no service
    @Mapping(target = "subsidiary", ignore = true) // será setado no service
    @Mapping(target = "professionals", ignore = true) // relacionamento many-to-many
    @Mapping(source = "active", target = "active")
    Item toEntity(ItemRequest dto);
}
