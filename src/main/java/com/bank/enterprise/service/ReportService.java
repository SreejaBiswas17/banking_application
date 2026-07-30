package com.bank.enterprise.service;

import com.bank.enterprise.dto.ReportDto;

import java.time.LocalDate;

public interface ReportService {
    ReportDto.FinancialSummaryDto generateFinancialSummary();
    ReportDto.AccountStatementDto generateAccountStatement(String accountNumber, LocalDate startDate, LocalDate endDate);
}
