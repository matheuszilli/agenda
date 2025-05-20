package com.agenda.app.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "chair_room_schedule_entry",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chair_room_id", "date"}))
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChairRoomScheduleEntry extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chair_room_id")
    private ChairRoom chairRoom;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @NotNull
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "closed", nullable = false)
    private boolean closed = false;

    @Column(name = "customized", nullable = false)
    private boolean customized = false;
}
