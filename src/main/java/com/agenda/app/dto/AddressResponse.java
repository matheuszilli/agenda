package com.agenda.app.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private String street;
    private String number;
    private String complement;
    private String neighbourhood;
    private String city;
    private String state;
    private String zipCode;
}
