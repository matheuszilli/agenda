package com.agenda.app.mapper;

import com.agenda.app.dto.ChairRoomResponse;
import com.agenda.app.dto.ChairRoomRequest;
import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.Subsidiary;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface ChairRoomMapper {

    /* ---------- entity → response ---------- */
    @Mapping(source = "subsidiary.name", target = "subsidiaryName")
    ChairRoomResponse toResponse(ChairRoom entity);

    /* ---------- request → entity ---------- */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subsidiary", expression = "java(subs)")
    @Mapping(source = "name",        target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "capacity",    target = "capacity")
    @Mapping(source = "roomNumber",  target = "roomNumber")
    ChairRoom toEntity(ChairRoomRequest req,
                       @Context Subsidiary subs);

    /* ---------- atualização parcial ---------- */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ChairRoomRequest req, @MappingTarget ChairRoom entity);

}