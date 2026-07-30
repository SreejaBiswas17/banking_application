package com.bank.enterprise.repository;

import com.bank.enterprise.model.SafeDepositLockerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SafeDepositLockerRepository extends JpaRepository<SafeDepositLockerEntity, Long> {
    Optional<SafeDepositLockerEntity> findByLockerNumber(String lockerNumber);
    List<SafeDepositLockerEntity> findByStatus(String status);
}
