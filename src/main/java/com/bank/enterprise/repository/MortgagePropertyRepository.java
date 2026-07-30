package com.bank.enterprise.repository;

import com.bank.enterprise.model.MortgagePropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MortgagePropertyRepository extends JpaRepository<MortgagePropertyEntity, Long> {
    Optional<MortgagePropertyEntity> findByLoan_LoanId(Long loanId);
}
