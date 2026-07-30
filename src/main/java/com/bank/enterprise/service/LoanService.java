package com.bank.enterprise.service;

import com.bank.enterprise.dto.LoanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {
    LoanDto.LoanResponse applyForLoan(LoanDto.LoanApplicationRequest request);
    LoanDto.LoanResponse approveLoan(Long loanId);
    LoanDto.LoanResponse disburseLoan(Long loanId, String destinationAccountNumber);
    LoanDto.LoanResponse getLoanById(Long loanId);
    List<LoanDto.LoanResponse> getLoansByCustomerId(Long customerId);
    void payLoanEmi(Long scheduleId, String sourceAccountNumber);
    Page<LoanDto.LoanResponse> getAllLoans(Pageable pageable);
}
