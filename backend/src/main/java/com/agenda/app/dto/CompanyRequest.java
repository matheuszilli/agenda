package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class CompanyRequest {
    @NotBlank private String name;
    @NotNull private AddressDTO address;
    private String phone;
    @NotBlank private String documentNumber;
}