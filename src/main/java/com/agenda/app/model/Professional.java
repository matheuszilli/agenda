package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;

@Entity
@Table(name = "professionals",
        indexes = {
                @Index(name = "idx_professional_fullname", columnList = "full_name")
        })
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class Professional extends BaseEntity {

    @NotBlank @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "full_name", nullable = false, length = 100, updatable = false)
    private String fullName;

    @NotBlank
    @Column(name = "document_number", length = 20)
    private String documentNumber;

    @NotBlank
    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Email @NotBlank
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name="available_start", nullable=false)
    private LocalTime availableStart;

    @Column(name="available_end", nullable=false)
    private LocalTime availableEnd;

    @ManyToOne
    @JoinColumn(name="subsidiary_id")
    private Subsidiary subsidiary;

    @PrePersist @PreUpdate
    private void buildFullName() {
        this.fullName = this.firstName + " " + this.lastName;
    }
}