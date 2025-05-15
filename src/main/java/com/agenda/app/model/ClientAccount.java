package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client_account")
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientAccount extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @NotNull
    @Column(name = "free_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal freeBalance = BigDecimal.ZERO;

    @NotNull
    @Column(name = "held_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal heldBalance = BigDecimal.ZERO;

    @NotNull
    @Column(name = "total_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBalance = BigDecimal.ZERO;

    @Column(name = "last_transaction_date")
    private Instant lastTransactionDate;

    @OneToMany(mappedBy = "clientAccount", cascade = CascadeType.ALL)
    private List<AccountTransaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "clientAccount", cascade = CascadeType.ALL)
    private List<AccountHold> holds = new ArrayList<>();

    /**
     * Updates the total balance based on free and held balance
     */
    @PrePersist
    @PreUpdate
    private void updateTotalBalance() {
        this.totalBalance = this.freeBalance.add(this.heldBalance);
        this.lastTransactionDate = Instant.now();
    }

    /**
     * Adds a transaction to this account
     */
    public void addTransaction(AccountTransaction transaction) {
        transactions.add(transaction);
        transaction.setClientAccount(this);
    }

    /**
     * Adds a hold to this account
     */
    public void addHold(AccountHold hold) {
        holds.add(hold);
        hold.setClientAccount(this);
    }
}