package com.bank.enterprise.repository;

import com.bank.enterprise.common.LoanStatus;
import com.bank.enterprise.model.LoanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    Optional<LoanEntity> findByLoanNumber(String loanNumber);

    List<LoanEntity> findByCustomer_CustomerId(Long customerId);

    Page<LoanEntity> findByStatus(LoanStatus status, Pageable pageable);
}
