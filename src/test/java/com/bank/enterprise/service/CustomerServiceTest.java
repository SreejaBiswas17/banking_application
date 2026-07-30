package com.bank.enterprise.service;

import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.UserRepository;
import com.bank.enterprise.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private UserEntity user;
    private CustomerEntity customer;
    private CustomerDto.CustomerCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder().userId(1L).username("testuser").email("test@example.com").build();
        customer = CustomerEntity.builder()
                .customerId(100L)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .taxIdNumber("123456789")
                .nationalId("987654321")
                .kycStatus(KycStatus.PENDING)
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .build();

        createRequest = CustomerDto.CustomerCreateRequest.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .taxIdNumber("123456789")
                .nationalId("987654321")
                .addressLine1("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .build();
    }

    @Test
    @DisplayName("Should successfully create customer profile for adult user")
    void createCustomerProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(customerRepository.findByUser_UserId(1L)).thenReturn(Optional.empty());
        when(customerRepository.findByTaxIdNumber("123456789")).thenReturn(Optional.empty());
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);

        CustomerDto.CustomerResponse response = customerService.createCustomerProfile(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getKycStatus()).isEqualTo(KycStatus.PENDING);
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should throw BankException when customer is under 18")
    void createCustomerProfile_UnderAge_ThrowsException() {
        createRequest.setDateOfBirth(LocalDate.now().minusYears(15));

        assertThatThrownBy(() -> customerService.createCustomerProfile(createRequest))
                .isInstanceOf(BankException.class)
                .hasMessageContaining("Customer must be at least 18 years old");
    }

    @Test
    @DisplayName("Should update KYC status")
    void updateKycStatus_Success() {
        when(customerRepository.findById(100L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer);

        CustomerDto.KycUpdateDto updateDto = CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build();
        CustomerDto.CustomerResponse response = customerService.updateKycStatus(100L, updateDto);

        assertThat(response.getKycStatus()).isEqualTo(KycStatus.VERIFIED);
        verify(auditService, times(1)).logAction(eq("UPDATE_KYC_STATUS"), anyString(), eq("CUSTOMER"), eq("100"), anyString(), eq("VERIFIED"));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid customer ID")
    void getCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
