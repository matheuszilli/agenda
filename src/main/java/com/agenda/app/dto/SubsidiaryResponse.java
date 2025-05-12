package com.agenda.app.dto;

import com.agenda.app.model.Address;
import com.agenda.app.model.SubsidiaryDaysOpen;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record SubsidiaryResponse(
        UUID id,
        String name,
        Address address,
        LocalTime openTime,
        LocalTime closeTime,
        Set<SubsidiaryDaysOpen> daysOpen,
        String documentNumber,
        UUID companyId
) {}