package com.agenda.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class CompanyResponse {
    private UUID id;
    private String name;
    private AddressDTO address;
    private String phone;
    private String documentNumber;
}