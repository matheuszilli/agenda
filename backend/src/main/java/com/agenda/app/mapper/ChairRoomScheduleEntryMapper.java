package com.agenda.app.mapper;

import com.agenda.app.dto.ChairRoomScheduleEntryRequest;
import com.agenda.app.dto.ChairRoomScheduleEntryResponse;
import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.ChairRoomScheduleEntry;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChairRoomScheduleEntryMapper {

    /* Request → Entity (usado para CREATE) */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chairRoom", expression = "java(chairRoom)")
    ChairRoomScheduleEntry toEntity(ChairRoomScheduleEntryRequest request,
                                    @Context ChairRoom chairRoom);

    /* Entity → Response */
    @Mapping(source = "chairRoom.id", target = "chairRoomId")
    ChairRoomScheduleEntryResponse toResponse(ChairRoomScheduleEntry entity);

    /* Atualização parcial (usado em PUT/PATCH) */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ChairRoomScheduleEntryRequest request,
                           @MappingTarget ChairRoomScheduleEntry entity);
}