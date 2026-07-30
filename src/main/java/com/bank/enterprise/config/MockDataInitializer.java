package com.bank.enterprise.config;

import com.bank.enterprise.common.*;
import com.bank.enterprise.model.*;
import com.bank.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Create Admin User
        UserEntity admin = UserEntity.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("AdminPass123#"))
                .email("admin@bank.com")
                .phoneNumber("18005550100")
                .userRole(UserRole.ADMIN)
                .isEnabled(true)
                .isLocked(false)
                .build();
        userRepository.save(admin);

        // 2. Create Customer 1 (John Smith)
        UserEntity user1 = UserEntity.builder()
                .username("john_smith")
                .passwordHash(passwordEncoder.encode("CustomerPass123#"))
                .email("john.smith@example.com")
                .phoneNumber("15551234567")
                .userRole(UserRole.CUSTOMER)
                .isEnabled(true)
                .isLocked(false)
                .build();
        UserEntity savedUser1 = userRepository.save(user1);

        CustomerEntity customer1 = CustomerEntity.builder()
                .user(savedUser1)
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1985, 3, 20))
                .taxIdNumber("TAX-99887711")
                .nationalId("NAT-11223344")
                .kycStatus(KycStatus.VERIFIED)
                .addressLine1("742 Evergreen Terrace")
                .city("Springfield")
                .state("IL")
                .postalCode("62701")
                .country("USA")
                .build();
        CustomerEntity savedCust1 = customerRepository.save(customer1);

        AccountEntity acc1 = AccountEntity.builder()
                .accountNumber("1001002001")
                .customer(savedCust1)
                .accountType(AccountType.SAVINGS)
                .currency(Currency.USD)
                .balance(new BigDecimal("25000.00"))
                .availableBalance(new BigDecimal("25000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .interestRate(Constants.DEFAULT_INTEREST_RATE_SAVINGS)
                .build();
        accountRepository.save(acc1);

        // 3. Create Customer 2 (Sarah Connor)
        UserEntity user2 = UserEntity.builder()
                .username("sarah_c")
                .passwordHash(passwordEncoder.encode("CustomerPass123#"))
                .email("sarah.connor@example.com")
                .phoneNumber("15559876543")
                .userRole(UserRole.CUSTOMER)
                .isEnabled(true)
                .isLocked(false)
                .build();
        UserEntity savedUser2 = userRepository.save(user2);

        CustomerEntity customer2 = CustomerEntity.builder()
                .user(savedUser2)
                .firstName("Sarah")
                .lastName("Connor")
                .dateOfBirth(LocalDate.of(1992, 8, 12))
                .taxIdNumber("TAX-55443322")
                .nationalId("NAT-99887766")
                .kycStatus(KycStatus.VERIFIED)
                .addressLine1("101 Cyberdyne Way")
                .city("Los Angeles")
                .state("CA")
                .postalCode("90210")
                .country("USA")
                .build();
        CustomerEntity savedCust2 = customerRepository.save(customer2);

        AccountEntity acc2 = AccountEntity.builder()
                .accountNumber("1001002002")
                .customer(savedCust2)
                .accountType(AccountType.CHECKING)
                .currency(Currency.USD)
                .balance(new BigDecimal("15000.00"))
                .availableBalance(new BigDecimal("15000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .overdraftLimit(new BigDecimal("2000.00"))
                .build();
        accountRepository.save(acc2);
    }
}
