package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.DateUtils;
import com.bank.enterprise.common.EncryptionUtils;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.UserRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public CustomerDto.CustomerResponse createCustomerProfile(CustomerDto.CustomerCreateRequest request) {
        if (!DateUtils.isAdult(request.getDateOfBirth())) {
            throw new BankException("Customer must be at least 18 years old to open an account");
        }

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        if (customerRepository.findByUser_UserId(request.getUserId()).isPresent()) {
            throw new BankException("Customer profile already exists for user ID: " + request.getUserId());
        }

        if (customerRepository.findByTaxIdNumber(request.getTaxIdNumber()).isPresent()) {
            throw new BankException("Tax ID already registered: " + request.getTaxIdNumber());
        }

        CustomerEntity customer = CustomerEntity.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .taxIdNumber(request.getTaxIdNumber())
                .nationalId(request.getNationalId())
                .kycStatus(KycStatus.PENDING)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "USA")
                .build();

        CustomerEntity savedCustomer = customerRepository.save(customer);

        auditService.logAction("CREATE_CUSTOMER_PROFILE", user.getUsername(), "CUSTOMER", savedCustomer.getCustomerId().toString(), null, "Profile created");

        return mapToCustomerResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto.CustomerResponse getCustomerById(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        return mapToCustomerResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto.CustomerResponse getCustomerByUserId(Long userId) {
        CustomerEntity customer = customerRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", userId));
        return mapToCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponse updateKycStatus(Long customerId, CustomerDto.KycUpdateDto kycUpdateDto) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        String oldStatus = customer.getKycStatus().name();
        customer.setKycStatus(kycUpdateDto.getStatus());
        CustomerEntity updated = customerRepository.save(customer);

        auditService.logAction("UPDATE_KYC_STATUS", "ADMIN", "CUSTOMER", customerId.toString(), oldStatus, kycUpdateDto.getStatus().name());

        return mapToCustomerResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDto.CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::mapToCustomerResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto.CustomerResponse> searchCustomers(String keyword) {
        return customerRepository.searchCustomers(keyword).stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    private CustomerDto.CustomerResponse mapToCustomerResponse(CustomerEntity entity) {
        return CustomerDto.CustomerResponse.builder()
                .customerId(entity.getCustomerId())
                .userId(entity.getUser().getUserId())
                .username(entity.getUser().getUsername())
                .email(entity.getUser().getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .taxIdNumberMasked(EncryptionUtils.maskNationalId(entity.getTaxIdNumber()))
                .nationalIdMasked(EncryptionUtils.maskNationalId(entity.getNationalId()))
                .kycStatus(entity.getKycStatus())
                .addressLine1(entity.getAddressLine1())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
