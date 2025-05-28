package com.agenda.app.dto;

public record AddressResponse(
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode
) {}