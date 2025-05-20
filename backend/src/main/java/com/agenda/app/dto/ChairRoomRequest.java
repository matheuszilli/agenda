package com.agenda.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChairRoomRequest {

    @NotBlank
    private String name;

    @NotNull
    private UUID subsidiaryId;

    private String description;

    @NotNull
    private Integer capacity;

    @NotBlank
    private String roomNumber;
}