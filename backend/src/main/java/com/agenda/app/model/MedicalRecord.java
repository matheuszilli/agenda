package com.agenda.app.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "medical_record")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MedicalRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "main_doctor_id", nullable = false)
    private User mainDoctor;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Item item;

    @Column(name = "description", length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "medical_record_photos_before", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "photo_url")
    private List<String> photosBefore;

    @ElementCollection
    @CollectionTable(name = "medical_record_photos_after", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "photo_url")
    private List<String> photosAfter;

    @Column(name = "finalized", nullable = false)
    private boolean finalized = false;
}