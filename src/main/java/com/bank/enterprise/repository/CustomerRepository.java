package com.bank.enterprise.repository;

import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.model.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByUser_UserId(Long userId);

    Optional<CustomerEntity> findByTaxIdNumber(String taxIdNumber);

    Optional<CustomerEntity> findByNationalId(String nationalId);

    List<CustomerEntity> findByKycStatus(KycStatus kycStatus);

    Page<CustomerEntity> findByKycStatus(KycStatus kycStatus, Pageable pageable);

    @Query("SELECT c FROM CustomerEntity c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.taxIdNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CustomerEntity> searchCustomers(@Param("keyword") String keyword);
}
