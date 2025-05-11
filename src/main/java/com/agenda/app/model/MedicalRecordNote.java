package com.agenda.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_record_notes")
public class MedicalRecordNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;
}
