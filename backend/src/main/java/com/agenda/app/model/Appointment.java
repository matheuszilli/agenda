package com.agenda.app.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appointments",
        indexes = {
                @Index(name = "idx_appointment_professional", columnList = "professional_id"),
                @Index(name = "idx_appointment_start", columnList = "start_time")
        })
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chair_room_id")
    private ChairRoom chairRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsidiary_id")
    private Subsidiary subsidiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AppointmentStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_order_id")
    private ServiceOrder serviceOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_doctor_id")
    private Professional mainDoctor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="appointment_assistants",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "professional_id")
    )
    private List<Professional> assistantDoctors;

//  Pensar se uma comanda pode ter mais de um agendamento ou não. Caso possa, habilitar essa regra
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_order_id")
//    private ServiceOrder serviceOrder;
}
