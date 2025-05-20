package com.agenda.app.dto;

import com.agenda.app.model.Address;
import com.agenda.app.model.SubsidiaryDaysOpen;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record SubsidiaryResponse(
        UUID id,
        String name,
        Address address,
        String documentNumber,
        UUID companyId,
        List<SubsidiaryScheduleEntryResponse> schedule
) {}