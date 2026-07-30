package com.bank.enterprise.service;

import com.bank.enterprise.dto.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerDto.CustomerResponse createCustomerProfile(CustomerDto.CustomerCreateRequest request);
    CustomerDto.CustomerResponse getCustomerById(Long customerId);
    CustomerDto.CustomerResponse getCustomerByUserId(Long userId);
    CustomerDto.CustomerResponse updateKycStatus(Long customerId, CustomerDto.KycUpdateDto kycUpdateDto);
    Page<CustomerDto.CustomerResponse> getAllCustomers(Pageable pageable);
    List<CustomerDto.CustomerResponse> searchCustomers(String keyword);
}
