package com.bank.enterprise.service;

import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.LoanStatus;
import com.bank.enterprise.common.LoanType;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.exception.LoanEligibilityException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.LoanEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.EmiScheduleRepository;
import com.bank.enterprise.repository.LoanRepository;
import com.bank.enterprise.service.impl.LoanServiceImpl;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private EmiScheduleRepository emiScheduleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuditService auditService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanServiceImpl loanService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder().userId(1L).username("bob").build();
        customer = CustomerEntity.builder()
                .customerId(10L)
                .user(user)
                .firstName("Bob")
                .lastName("Marley")
                .kycStatus(KycStatus.VERIFIED)
                .build();
    }

    @Test
    @DisplayName("Should successfully submit loan application for verified customer")
    void applyForLoan_Success() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        LoanEntity loan = LoanEntity.builder()
                .loanId(100L)
                .loanNumber("LN-12345678")
                .customer(customer)
                .loanType(LoanType.PERSONAL)
                .principalAmount(new BigDecimal("10000.00"))
                .outstandingPrincipal(new BigDecimal("10000.00"))
                .annualInterestRate(new BigDecimal("0.12"))
                .tenureMonths(12)
                .monthlyEmi(new BigDecimal("888.49"))
                .status(LoanStatus.SUBMITTED)
                .build();

        when(loanRepository.save(any(LoanEntity.class))).thenReturn(loan);

        LoanDto.LoanApplicationRequest req = LoanDto.LoanApplicationRequest.builder()
                .customerId(10L)
                .loanType(LoanType.PERSONAL)
                .principalAmount(new BigDecimal("10000.00"))
                .tenureMonths(12)
                .build();

        LoanDto.LoanResponse res = loanService.applyForLoan(req);

        assertThat(res).isNotNull();
        assertThat(res.getStatus()).isEqualTo(LoanStatus.SUBMITTED);
        assertThat(res.getPrincipalAmount()).isEqualByComparingTo("10000.00");
    }

    @Test
    @DisplayName("Should throw LoanEligibilityException when customer is unverified")
    void applyForLoan_Unverified_ThrowsException() {
        customer.setKycStatus(KycStatus.PENDING);
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        LoanDto.LoanApplicationRequest req = LoanDto.LoanApplicationRequest.builder()
                .customerId(10L)
                .loanType(LoanType.PERSONAL)
                .principalAmount(new BigDecimal("10000.00"))
                .tenureMonths(12)
                .build();

        assertThatThrownBy(() -> loanService.applyForLoan(req))
                .isInstanceOf(LoanEligibilityException.class);
    }
}
