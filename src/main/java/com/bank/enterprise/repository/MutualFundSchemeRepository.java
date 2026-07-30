package com.bank.enterprise.repository;

import com.bank.enterprise.model.MutualFundHoldingEntity;
import com.bank.enterprise.model.MutualFundSchemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MutualFundSchemeRepository extends JpaRepository<MutualFundSchemeEntity, Long> {
    Optional<MutualFundSchemeEntity> findBySchemeCode(String schemeCode);
    List<MutualFundSchemeEntity> findByCategory(String category);
}
