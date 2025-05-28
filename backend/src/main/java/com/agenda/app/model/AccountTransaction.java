package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account_transactions")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_account_id", nullable = false)
    private ClientAccount clientAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private AccountTransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "previous_free_balance", precision = 10, scale = 2)
    private BigDecimal previousFreeBalance;

    @Column(name = "previous_held_balance", precision = 10, scale = 2)
    private BigDecimal previousHeldBalance;

    @Column(name = "new_free_balance", precision = 10, scale = 2)
    private BigDecimal newFreeBalance;

    @Column(name = "new_held_balance", precision = 10, scale = 2)
    private BigDecimal newHeldBalance;
}