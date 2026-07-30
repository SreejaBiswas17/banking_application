package com.bank.enterprise.benchmark;

import com.bank.enterprise.common.*;
import com.bank.enterprise.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SyntheticDataBenchmarkSuite {

    @Test
    @DisplayName("Generate 100 Synthetic Customer Profiles Benchmark")
    void benchmarkSyntheticCustomers() {
        List<CustomerEntity> customers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            UserEntity user = UserEntity.builder()
                    .userId((long) i)
                    .username("synth_user_" + i)
                    .passwordHash("hashed_pass_" + i)
                    .email("synth_" + i + "@benchmark.com")
                    .phoneNumber("1800555" + String.format("%04d", i))
                    .userRole(UserRole.CUSTOMER)
                    .isEnabled(true)
                    .isLocked(false)
                    .build();

            CustomerEntity customer = CustomerEntity.builder()
                    .customerId((long) i)
                    .user(user)
                    .firstName("SyntheticFirst" + i)
                    .lastName("SyntheticLast" + i)
                    .dateOfBirth(LocalDate.of(1980 + (i % 20), 1 + (i % 12), 1 + (i % 28)))
                    .taxIdNumber("TAX-BENCH-" + i)
                    .nationalId("NAT-BENCH-" + i)
                    .kycStatus(i % 2 == 0 ? KycStatus.VERIFIED : KycStatus.PENDING)
                    .addressLine1("Street " + i)
                    .city("City " + i)
                    .state("State " + i)
                    .postalCode(String.format("%05d", i))
                    .country("USA")
                    .build();

            customers.add(customer);
        }

        assertThat(customers).hasSize(100);
    }

    @Test
    @DisplayName("Generate 500 Synthetic Financial Transactions Benchmark")
    void benchmarkSyntheticTransactions() {
        List<TransactionEntity> txList = new ArrayList<>();
        AccountEntity source = AccountEntity.builder().accountId(1L).accountNumber("10001").build();
        AccountEntity dest = AccountEntity.builder().accountId(2L).accountNumber("10002").build();

        for (int i = 1; i <= 500; i++) {
            TransactionEntity tx = TransactionEntity.builder()
                    .transactionId((long) i)
                    .transactionReference("REF-BENCH-" + i)
                    .sourceAccount(source)
                    .destinationAccount(dest)
                    .transactionType(i % 2 == 0 ? TransactionType.INTERNAL_TRANSFER : TransactionType.DEPOSIT)
                    .amount(new BigDecimal("10.00").multiply(new BigDecimal(i)))
                    .feeAmount(BigDecimal.ZERO)
                    .currency(Currency.USD)
                    .status(TransactionStatus.COMPLETED)
                    .description("Benchmark Synthetic Transaction #" + i)
                    .initiatedAt(LocalDateTime.now().minusMinutes(i))
                    .completedAt(LocalDateTime.now())
                    .build();

            txList.add(tx);
        }

        assertThat(txList).hasSize(500);
    }
}
