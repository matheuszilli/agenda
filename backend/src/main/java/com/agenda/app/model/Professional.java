package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professionals", indexes = {
                @Index(name = "idx_professional_fullname", columnList = "full_name")
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class Professional extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        @Size(max = 50)
        @Column(name = "first_name", nullable = false, length = 50)
        private String firstName;

        @Size(max = 50)
        @Column(name = "last_name", nullable = false, length = 50)
        private String lastName;

        @Column(name = "full_name", nullable = false, length = 100, updatable = false)
        private String fullName;

        @Column(name = "document_number", length = 20)
        private String documentNumber;

        @Embedded
        private Address address;

        @Column(name = "phone", length = 20)
        private String phone;

        @Email
        @Column(name = "email", nullable = false, length = 100)
        private String email;

        @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<ProfessionalScheduleEntry> scheduleEntries = new HashSet<>();

        @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<ProfessionalServiceCfg> serviceConfigs = new HashSet<>();

        @ManyToMany
        @JoinTable(name = "professional_services", joinColumns = @JoinColumn(name = "professional_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
        private Set<Item> services = new HashSet<>();

        @ManyToOne
        @JoinColumn(name = "subsidiary_id")
        private Subsidiary subsidiary;

        @PrePersist
        @PreUpdate
        private void buildFullName() {
                this.fullName = this.firstName + " " + this.lastName;
        }
}