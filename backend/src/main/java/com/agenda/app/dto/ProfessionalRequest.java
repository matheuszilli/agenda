package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalRequest {

    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String documentNumber;
    @NotNull  private AddressDTO address;
    @NotBlank private String phone;
    @Email @NotBlank private String email;
    @NotNull private UUID subsidiaryId;
    private UUID userId;

    private List<ProfessionalServiceCfgRequest> services;
}