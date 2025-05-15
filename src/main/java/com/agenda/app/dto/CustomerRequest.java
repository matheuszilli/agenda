package com.agenda.app.dto;

import com.agenda.app.model.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.UUID;

@Data
public class CustomerRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    @NotBlank
    private String documentNumber;
    @NotBlank
    private String phone;
    @NotNull
    private AddressRequest address;
    @NotNull
    private UUID company;
    

}
