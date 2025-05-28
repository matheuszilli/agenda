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

    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "subsidiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubsidiaryScheduleEntry> scheduleEntries = new HashSet<>();

    // Campos de horários padrão (agora sem a anotação @NotNull)
    @Column(name = "open_time")
    private LocalTime openTime;
    
    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "document_number", length = 20)
    private String documentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Relacionamentos com outras entidades
    @OneToMany(mappedBy = "subsidiary", cascade = CascadeType.ALL)
    private Set<ChairRoom> chairRooms = new HashSet<>();
    
    @OneToMany(mappedBy = "subsidiary", cascade = CascadeType.ALL)
    private Set<Professional> professionals = new HashSet<>();
    
    @OneToMany(mappedBy = "subsidiary", cascade = CascadeType.ALL)
    private Set<Item> services = new HashSet<>();
}