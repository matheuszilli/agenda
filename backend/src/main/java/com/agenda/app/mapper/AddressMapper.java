package com.agenda.app.mapper;

import com.agenda.app.dto.AddressDTO;
import com.agenda.app.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressDTO dto);
    
    AddressDTO toDto(Address entity);
    
    void updateAddressFromDto(AddressDTO dto, @MappingTarget Address entity);
}