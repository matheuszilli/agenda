package com.agenda.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_record_notes")
public class MedicalRecordNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;
}
