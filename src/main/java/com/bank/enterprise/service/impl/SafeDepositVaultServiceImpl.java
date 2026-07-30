package com.bank.enterprise.service.impl;

import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.SafeDepositLockerEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.SafeDepositLockerRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.SafeDepositVaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SafeDepositVaultServiceImpl implements SafeDepositVaultService {

    private final SafeDepositLockerRepository lockerRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public SafeDepositLockerEntity rentLocker(Long customerId, String lockerSize) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<SafeDepositLockerEntity> vacantLockers = lockerRepository.findByStatus("VACANT");
        SafeDepositLockerEntity vacant = vacantLockers.stream()
                .filter(l -> l.getLockerSize().equalsIgnoreCase(lockerSize))
                .findFirst()
                .orElseThrow(() -> new BankException("No vacant lockers available for size: " + lockerSize));

        vacant.setCustomer(customer);
        vacant.setStatus("RENTED");
        vacant.setRentalExpiryDate(LocalDate.now().plusYears(1));

        SafeDepositLockerEntity saved = lockerRepository.save(vacant);
        auditService.logAction("RENT_SAFE_DEPOSIT_LOCKER", customer.getUser().getUsername(), "SAFE_DEPOSIT_LOCKER", saved.getLockerId().toString(), null, "Rented Locker: " + saved.getLockerNumber());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SafeDepositLockerEntity> getAvailableLockers() {
        return lockerRepository.findByStatus("VACANT");
    }
}
