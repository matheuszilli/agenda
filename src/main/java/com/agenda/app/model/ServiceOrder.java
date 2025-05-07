package com.agenda.app.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_order")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @OneToOne(mappedBy = "serviceOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
}