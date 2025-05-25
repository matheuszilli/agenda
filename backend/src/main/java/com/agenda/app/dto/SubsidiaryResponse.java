package com.agenda.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubsidiaryResponse {
    private UUID id;
    private String name;
    private AddressDTO address;
    private String documentNumber;
    private UUID companyId;
    private List<SubsidiaryScheduleEntryResponse> schedule;
    private LocalTime openTime;
    private LocalTime closeTime;
}