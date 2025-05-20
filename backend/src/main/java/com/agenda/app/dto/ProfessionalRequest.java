package com.agenda.app.dto;

import com.agenda.app.model.Address;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProfessionalRequest {

    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String documentNumber;
    @NotNull  private Address address;
    @NotBlank private String phone;
    @Email @NotBlank private String email;
    @NotNull private UUID subsidiaryId;
    private UUID userId;

    private List<ProfessionalServiceCfgRequest> services;
}