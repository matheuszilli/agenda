package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subsidiaries")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subsidiary extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private Address address;

    @Column(name="open_time", nullable=false)
    private LocalTime openTime;

    @Column(name="close_time", nullable=false)
    private LocalTime closeTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "day_open")
    private Set<SubsidiaryDaysOpen> daysOpen = new HashSet<>();


    @NotBlank
    @Column(name = "document_number", length = 20)
    private String documentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}