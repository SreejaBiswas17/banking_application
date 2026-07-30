package com.bank.enterprise.service;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.dto.AccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDto.AccountResponse createAccount(AccountDto.AccountCreateRequest request);
    AccountDto.AccountResponse getAccountByNumber(String accountNumber);
    AccountDto.AccountResponse getAccountById(Long accountId);
    List<AccountDto.AccountResponse> getAccountsByCustomerId(Long customerId);
    AccountDto.BalanceResponse checkBalance(String accountNumber);
    AccountDto.AccountResponse updateAccountStatus(Long accountId, AccountStatus newStatus);
    Page<AccountDto.AccountResponse> getAllAccounts(Pageable pageable);
    void applyInterestToSavingsAccounts();
}
