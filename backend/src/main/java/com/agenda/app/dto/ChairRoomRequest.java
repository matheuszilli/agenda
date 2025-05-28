package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChairRoomRequest(
    @NotBlank String name,
    @NotNull UUID subsidiaryId,
    String description,
    @NotNull Integer capacity,
    @NotBlank String roomNumber
) {}