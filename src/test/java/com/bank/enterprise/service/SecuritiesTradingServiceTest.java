package com.bank.enterprise.service;

import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.SecuritiesPortfolioEntity;
import com.bank.enterprise.model.StockOrderEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.SecuritiesPortfolioRepository;
import com.bank.enterprise.repository.StockOrderRepository;
import com.bank.enterprise.service.impl.SecuritiesTradingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecuritiesTradingServiceTest {

    @Mock
    private SecuritiesPortfolioRepository portfolioRepository;

    @Mock
    private StockOrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SecuritiesTradingServiceImpl securitiesService;

    private CustomerEntity customer;
    private AccountEntity account;
    private SecuritiesPortfolioEntity portfolio;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder().customerId(1L).build();
        account = AccountEntity.builder().accountId(10L).accountNumber("100200300").availableBalance(new BigDecimal("100000.00")).build();
        portfolio = SecuritiesPortfolioEntity.builder().portfolioId(100L).customer(customer).dematAccountNumber("DEMAT-1234").totalPortfolioValue(BigDecimal.ZERO).build();
    }

    @Test
    @DisplayName("Should successfully execute stock buy order")
    void executeStockTrade_Buy_Success() {
        when(portfolioRepository.findByCustomer_CustomerId(1L)).thenReturn(Optional.of(portfolio));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        StockOrderEntity order = StockOrderEntity.builder()
                .orderId(500L)
                .orderReference("ORD-12345678")
                .portfolio(portfolio)
                .tickerSymbol("AAPL")
                .orderType("BUY")
                .quantity(10)
                .executionPrice(new BigDecimal("150.00"))
                .status("EXECUTED")
                .build();

        when(orderRepository.save(any(StockOrderEntity.class))).thenReturn(order);

        StockOrderEntity result = securitiesService.executeStockTrade(1L, 10L, "AAPL", "BUY", 10, new BigDecimal("150.00"));

        assertThat(result).isNotNull();
        assertThat(result.getOrderReference()).isEqualTo("ORD-12345678");
        verify(transactionService, times(1)).transferFunds(any());
    }
}
