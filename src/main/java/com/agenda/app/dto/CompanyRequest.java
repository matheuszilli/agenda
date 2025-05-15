package com.agenda.app.dto;


import com.agenda.app.model.Company;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class CompanyRequest{
        @NotBlank private String name;
        @NotNull private AddressRequest address;
        private String phone;
        @NotBlank private String documentNumber;
}
