package com.bank.enterprise.repository;

import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should search customers by keyword")
    void searchCustomers() {
        UserEntity u1 = userRepository.save(UserEntity.builder().username("u1").email("u1@t.com").phoneNumber("1").passwordHash("p").userRole(UserRole.CUSTOMER).build());
        UserEntity u2 = userRepository.save(UserEntity.builder().username("u2").email("u2@t.com").phoneNumber("2").passwordHash("p").userRole(UserRole.CUSTOMER).build());

        customerRepository.save(CustomerEntity.builder().user(u1).firstName("Alexander").lastName("Hamilton").dateOfBirth(LocalDate.of(1980, 1, 1)).taxIdNumber("TAX111").nationalId("NAT111").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerRepository.save(CustomerEntity.builder().user(u2).firstName("Benjamin").lastName("Franklin").dateOfBirth(LocalDate.of(1980, 1, 1)).taxIdNumber("TAX222").nationalId("NAT222").addressLine1("a").city("c").state("s").postalCode("p").build());

        List<CustomerEntity> results = customerRepository.searchCustomers("Alex");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Hamilton");
    }
}
