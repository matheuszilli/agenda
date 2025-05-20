package com.agenda.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public record ChairRoomResponse (
    UUID id,
    String name,
    String subsidiaryName,
    UUID subsidiaryId,
    String description,
    Integer capacity,
    Integer roomNumber
) {}
