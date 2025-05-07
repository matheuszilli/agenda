package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "subsidiaries")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subsidiary extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(name="open_time", nullable=false)
    private LocalTime openTime;

    @Column(name="close_time", nullable=false)
    private LocalTime closeTime;

    @Column(name="days_open", nullable=false)
    private SubsidiaryDaysOpen daysOpen;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}