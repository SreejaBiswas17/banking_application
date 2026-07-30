package com.bank.enterprise.repository;

import com.bank.enterprise.common.TransactionStatus;
import com.bank.enterprise.common.TransactionType;
import com.bank.enterprise.model.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Optional<TransactionEntity> findByTransactionReference(String transactionReference);

    Page<TransactionEntity> findBySourceAccount_AccountIdOrDestinationAccount_AccountId(Long sourceId, Long destId, Pageable pageable);

    @Query("SELECT t FROM TransactionEntity t WHERE (t.sourceAccount.accountId = :accountId OR t.destinationAccount.accountId = :accountId) AND t.initiatedAt BETWEEN :startDate AND :endDate ORDER BY t.initiatedAt DESC")
    Page<TransactionEntity> findAccountStatement(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.sourceAccount.accountId = :accountId AND t.transactionType = :type AND t.initiatedAt >= :sinceDate AND t.status = 'COMPLETED'")
    BigDecimal calculateDailyTransferSum(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("sinceDate") LocalDateTime sinceDate);

    List<TransactionEntity> findByStatus(TransactionStatus status);
}
