// src/main/java/com/agenda/app/model/ProfessionalServiceCfg.java
package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professional_service_cfg",
        uniqueConstraints = @UniqueConstraint(columnNames = {"professional_id","service_id"}))
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class ProfessionalServiceCfg extends BaseEntity {

    /* ========= CHAVES ========= */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id")
    private Professional professional;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Item service;     // <-- troque Item → BusinessService (ou o nome real)


    /* ========= CUSTOMIZAÇÕES ========= */
    @DecimalMin("0.0")
    @Column(precision = 10, scale = 2)
    private BigDecimal customPrice;

    private Integer customDurationMinutes;

    /* ========= COMISSÃO ========= */
    @DecimalMin("0.0")
    @Column(precision = 5, scale = 2)
    private BigDecimal commissionPct;      // 0-100

    @DecimalMin("0.0")
    @Column(precision = 10, scale = 2)
    private BigDecimal commissionFixed;    // valor fixo
}