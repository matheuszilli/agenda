package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "subsidiary_schedule_entry")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class SubsidiaryScheduleEntry extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;

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

    public SubsidiaryScheduleEntry(Subsidiary subsidiary, LocalDate date) {
        this.subsidiary = subsidiary;
        this.date = date;
    }

}
