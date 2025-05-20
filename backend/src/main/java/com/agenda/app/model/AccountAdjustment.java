package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account_adjustments")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountAdjustment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private AccountTransaction transaction;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approved_by_user_id", nullable = false)
    private User approvedBy;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(name = "is_refund", nullable = false)
    private boolean isRefund = false;
}