package com.agenda.app.mapper;

import com.agenda.app.dto.AddressRequest;
import com.agenda.app.dto.AddressResponse;
import com.agenda.app.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address entity);

    void updateAddressFromRequest(AddressRequest request, @MappingTarget Address entity);
}