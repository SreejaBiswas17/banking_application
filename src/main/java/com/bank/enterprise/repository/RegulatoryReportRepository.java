package com.bank.enterprise.repository;

import com.bank.enterprise.model.RegulatoryReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegulatoryReportRepository extends JpaRepository<RegulatoryReportEntity, Long> {
    List<RegulatoryReportEntity> findByReportType(String reportType);
}
