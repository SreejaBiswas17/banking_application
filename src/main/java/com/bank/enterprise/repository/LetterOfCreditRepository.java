package com.bank.enterprise.repository;

import com.bank.enterprise.model.LetterOfCreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LetterOfCreditRepository extends JpaRepository<LetterOfCreditEntity, Long> {
    Optional<LetterOfCreditEntity> findByLcNumber(String lcNumber);
    List<LetterOfCreditEntity> findByApplicantCustomer_CustomerId(Long customerId);
}
