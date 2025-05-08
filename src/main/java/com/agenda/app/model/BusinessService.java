package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BusinessService extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationInMinutes;

    @Column(name = "requires_pre_payment", nullable = false)
    private boolean requiresPrePayment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}