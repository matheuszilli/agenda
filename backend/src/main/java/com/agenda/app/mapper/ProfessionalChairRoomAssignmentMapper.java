package com.agenda.app.mapper;

import com.agenda.app.dto.ProfessionalChairRoomAssignmentResponse;
import com.agenda.app.model.ProfessionalChairRoomAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para ProfessionalChairRoomAssignment
 */
@Mapper(componentModel = "spring")
public interface ProfessionalChairRoomAssignmentMapper {
    
    @Mapping(source = "professional.id", target = "professionalId")
    @Mapping(source = "professional.fullName", target = "professionalName")
    @Mapping(source = "chairRoom.id", target = "chairRoomId")
    @Mapping(source = "chairRoom.name", target = "chairRoomName")
    ProfessionalChairRoomAssignmentResponse toResponse(ProfessionalChairRoomAssignment entity);
    
    List<ProfessionalChairRoomAssignmentResponse> toResponseList(List<ProfessionalChairRoomAssignment> entities);
}