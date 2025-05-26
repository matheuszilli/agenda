package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDTO {
    @NotBlank
    private String street;
    
    @NotBlank
    private String number;
    
    @NotNull
    private String complement;
    private String neighbourhood;
    
    @NotBlank
    private String city;
    
    @NotBlank
    private String state;
    
    @NotBlank
    private String zipCode;
}