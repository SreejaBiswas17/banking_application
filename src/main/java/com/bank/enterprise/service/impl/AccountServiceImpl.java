package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Constants;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public AccountDto.AccountResponse createAccount(AccountDto.AccountCreateRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        if (customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new BankException("Cannot open account. Customer KYC status is " + customer.getKycStatus());
        }

        BigDecimal initialDeposit = request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO;
        if (request.getAccountType() == AccountType.SAVINGS && initialDeposit.compareTo(Constants.MIN_ACCOUNT_BALANCE_SAVINGS) < 0) {
            throw new BankException("Minimum initial deposit for Savings account is " + Constants.MIN_ACCOUNT_BALANCE_SAVINGS);
        }

        String accountNumber = generateUniqueAccountNumber();

        BigDecimal interestRate = request.getAccountType() == AccountType.SAVINGS ? Constants.DEFAULT_INTEREST_RATE_SAVINGS : BigDecimal.ZERO;
        BigDecimal overdraftLimit = request.getAccountType() == AccountType.CHECKING ? new BigDecimal("1000.00") : BigDecimal.ZERO;

        AccountEntity account = AccountEntity.builder()
                .accountNumber(accountNumber)
                .customer(customer)
                .accountType(request.getAccountType())
                .currency(request.getCurrency() != null ? request.getCurrency() : Currency.USD)
                .balance(initialDeposit)
                .availableBalance(initialDeposit)
                .accountStatus(AccountStatus.ACTIVE)
                .overdraftLimit(overdraftLimit)
                .interestRate(interestRate)
                .build();

        AccountEntity savedAccount = accountRepository.save(account);

        auditService.logAction("CREATE_ACCOUNT", customer.getUser().getUsername(), "ACCOUNT", savedAccount.getAccountId().toString(), null, "Account created: " + accountNumber);

        return mapToAccountResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.AccountResponse getAccountByNumber(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.AccountResponse getAccountById(Long accountId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto.AccountResponse> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomer_CustomerId(customerId).stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto.BalanceResponse checkBalance(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));

        return AccountDto.BalanceResponse.builder()
                .accountNumber(account.getAccountNumber())
                .ledgerBalance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .currency(account.getCurrency())
                .build();
    }

    @Override
    @Transactional
    public AccountDto.AccountResponse updateAccountStatus(Long accountId, AccountStatus newStatus) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        String oldStatus = account.getAccountStatus().name();
        account.setAccountStatus(newStatus);
        AccountEntity updated = accountRepository.save(account);

        auditService.logAction("UPDATE_ACCOUNT_STATUS", "SYSTEM", "ACCOUNT", accountId.toString(), oldStatus, newStatus.name());

        return mapToAccountResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountDto.AccountResponse> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(this::mapToAccountResponse);
    }

    @Override
    @Transactional
    public void applyInterestToSavingsAccounts() {
        List<AccountEntity> accounts = accountRepository.findAll();
        for (AccountEntity account : accounts) {
            if (account.getAccountType() == AccountType.SAVINGS && account.getAccountStatus() == AccountStatus.ACTIVE) {
                BigDecimal monthlyInterest = account.getBalance()
                        .multiply(account.getInterestRate())
                        .divide(new BigDecimal("12"), 4, RoundingMode.HALF_UP);

                account.setBalance(account.getBalance().add(monthlyInterest));
                account.setAvailableBalance(account.getAvailableBalance().add(monthlyInterest));
                accountRepository.save(account);

                auditService.logAction("MONTHLY_INTEREST_CREDIT", "SYSTEM", "ACCOUNT", account.getAccountId().toString(), null, "Credited " + monthlyInterest);
            }
        }
    }

    private String generateUniqueAccountNumber() {
        String num;
        do {
            long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
            num = String.valueOf(number);
        } while (accountRepository.findByAccountNumber(num).isPresent());
        return num;
    }

    private AccountDto.AccountResponse mapToAccountResponse(AccountEntity entity) {
        String fullName = entity.getCustomer().getFirstName() + " " + entity.getCustomer().getLastName();
        return AccountDto.AccountResponse.builder()
                .accountId(entity.getAccountId())
                .accountNumber(entity.getAccountNumber())
                .customerId(entity.getCustomer().getCustomerId())
                .customerFullName(fullName)
                .accountType(entity.getAccountType())
                .currency(entity.getCurrency())
                .balance(entity.getBalance())
                .availableBalance(entity.getAvailableBalance())
                .accountStatus(entity.getAccountStatus())
                .overdraftLimit(entity.getOverdraftLimit())
                .interestRate(entity.getInterestRate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
