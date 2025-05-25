package com.agenda.app.mapper;

import com.agenda.app.dto.*;
import com.agenda.app.model.Item;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "company.id", target = "companyId")
    ItemResponse toResponse(Item entity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true) // id é gerado
    Item toEntity(ItemRequest dto);
}
