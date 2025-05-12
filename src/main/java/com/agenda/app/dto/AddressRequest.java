package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest{
        @NotBlank private String street;
        @NotBlank private String number;
        private   String complement;
        @NotBlank private String neighbourhood;
        @NotBlank private String city;
        @NotBlank private String state;
        @NotBlank private String zipCode;
}
