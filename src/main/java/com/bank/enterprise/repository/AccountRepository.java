package com.bank.enterprise.repository;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.model.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.accountNumber = :accountNumber")
    Optional<AccountEntity> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

    List<AccountEntity> findByCustomer_CustomerId(Long customerId);

    List<AccountEntity> findByCustomer_CustomerIdAndAccountStatus(Long customerId, AccountStatus accountStatus);

    Page<AccountEntity> findByAccountType(AccountType accountType, Pageable pageable);

    @Query("SELECT SUM(a.balance) FROM AccountEntity a WHERE a.customer.customerId = :customerId AND a.accountStatus = 'ACTIVE'")
    BigDecimal getTotalBalanceByCustomerId(@Param("customerId") Long customerId);
}
