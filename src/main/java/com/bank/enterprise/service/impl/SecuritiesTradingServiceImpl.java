package com.bank.enterprise.service.impl;

import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.SecuritiesPortfolioEntity;
import com.bank.enterprise.model.StockOrderEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.SecuritiesPortfolioRepository;
import com.bank.enterprise.repository.StockOrderRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.SecuritiesTradingService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecuritiesTradingServiceImpl implements SecuritiesTradingService {

    private final SecuritiesPortfolioRepository portfolioRepository;
    private final StockOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public SecuritiesPortfolioEntity openDematPortfolio(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        String dematNum = "DEMAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        SecuritiesPortfolioEntity portfolio = SecuritiesPortfolioEntity.builder()
                .dematAccountNumber(dematNum)
                .customer(customer)
                .totalPortfolioValue(BigDecimal.ZERO)
                .build();

        SecuritiesPortfolioEntity saved = portfolioRepository.save(portfolio);
        auditService.logAction("OPEN_DEMAT_PORTFOLIO", customer.getUser().getUsername(), "DEMAT_PORTFOLIO", saved.getPortfolioId().toString(), null, "Opened: " + dematNum);
        return saved;
    }

    @Override
    @Transactional
    public StockOrderEntity executeStockTrade(Long customerId, Long accountId, String tickerSymbol, String orderType, int quantity, BigDecimal price) {
        SecuritiesPortfolioEntity portfolio = portfolioRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("SecuritiesPortfolio", "customerId", customerId));

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        BigDecimal totalCost = price.multiply(new BigDecimal(quantity));

        if ("BUY".equalsIgnoreCase(orderType)) {
            if (account.getAvailableBalance().compareTo(totalCost) < 0) {
                throw new BankException("Insufficient balance for stock purchase execution");
            }
            transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                    .sourceAccountNumber(account.getAccountNumber())
                    .destinationAccountNumber("BANK_BROKERAGE_CLEARING_POOL")
                    .amount(totalCost)
                    .description("Stock Order Buy: " + quantity + " " + tickerSymbol)
                    .build());
            portfolio.setTotalPortfolioValue(portfolio.getTotalPortfolioValue().add(totalCost));
        } else {
            transactionService.deposit(TransactionDto.DepositRequest.builder()
                    .accountNumber(account.getAccountNumber())
                    .amount(totalCost)
                    .description("Stock Order Sell Payout: " + quantity + " " + tickerSymbol)
                    .build());
            portfolio.setTotalPortfolioValue(portfolio.getTotalPortfolioValue().subtract(totalCost));
        }

        portfolioRepository.save(portfolio);

        String ref = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        StockOrderEntity order = StockOrderEntity.builder()
                .orderReference(ref)
                .portfolio(portfolio)
                .tickerSymbol(tickerSymbol)
                .orderType(orderType.toUpperCase())
                .quantity(quantity)
                .executionPrice(price)
                .status("EXECUTED")
                .build();

        StockOrderEntity saved = orderRepository.save(order);
        auditService.logAction("EXECUTE_STOCK_TRADE", portfolio.getCustomer().getUser().getUsername(), "STOCK_ORDER", saved.getOrderId().toString(), null, "Executed: " + ref);
        return saved;
    }
}
