package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.TransactionType;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CustomerService;
import com.bank.enterprise.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ConcurrencyAndLockingStressTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Stress Test: 10 Concurrent Threads Depositing to Same Account simultaneously")
    void stressTest_ConcurrentDeposits() throws InterruptedException {
        // Setup Account
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("stress_user").password("Pass123#").email("stress@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u.getUserId()).firstName("Stress").lastName("Test").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-STRESS").nationalId("NAT-STRESS").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse account = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("1000.00")).build());

        int threadCount = 10;
        BigDecimal depositPerThread = new BigDecimal("100.00");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    transactionService.deposit(TransactionDto.DepositRequest.builder()
                            .accountNumber(account.getAccountNumber())
                            .amount(depositPerThread)
                            .description("Concurrent Deposit Thread")
                            .build());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Concurrent Deposit Error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(threadCount);
        AccountDto.BalanceResponse finalBal = accountService.checkBalance(account.getAccountNumber());
        assertThat(finalBal.getAvailableBalance()).isEqualByComparingTo("2000.00");
    }
}
