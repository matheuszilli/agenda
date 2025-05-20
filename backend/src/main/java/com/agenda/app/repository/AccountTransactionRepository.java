package com.agenda.app.repository;

import com.agenda.app.model.AccountTransaction;
import com.agenda.app.model.AccountTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, UUID> {

    Page<AccountTransaction> findByClientAccountId(UUID clientAccountId, Pageable pageable);

    Page<AccountTransaction> findByClientAccountCustomerId(UUID customerId, Pageable pageable);

    List<AccountTransaction> findByClientAccountIdAndTransactionType(
            UUID clientAccountId,
            AccountTransactionType transactionType);

    @Query("SELECT t FROM AccountTransaction t WHERE t.clientAccount.id = :accountId " +
            "AND t.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY t.createdAt DESC")
    List<AccountTransaction> findTransactionsInPeriod(
            @Param("accountId") UUID accountId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}