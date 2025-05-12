package com.agenda.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CompanyResponse {
    private UUID id;
    private String name;
    private AddressResponse address;   // << era String
    private String phone;
    private String documentNumber;
}
