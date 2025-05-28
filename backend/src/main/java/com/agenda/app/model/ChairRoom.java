package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chair_rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "subsidiary_id"}))
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChairRoom extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subsidiary_id", nullable = false)
    private Subsidiary subsidiary;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "capacity")
    private Integer capacity;
}