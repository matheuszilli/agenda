package com.agenda.app.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentRequest{
        @NotNull
        private UUID customerId;
        @NotNull
        private UUID professionalId;
        @NotBlank
        private String serviceId;
        @NotBlank
        private String chairRoomId;
        @NotNull
        private UUID itemId;
        @NotNull
        private UUID subsidiaryId;
        @NotNull
        private UUID companyId;
        @NotNull
        private LocalDateTime startTime;
        @NotNull private LocalDateTime endTime;
        private String notes;
        private UUID paymentId;
}
