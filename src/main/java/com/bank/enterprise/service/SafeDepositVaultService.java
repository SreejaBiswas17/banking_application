package com.bank.enterprise.service;

import com.bank.enterprise.model.SafeDepositLockerEntity;

import java.util.List;

public interface SafeDepositVaultService {
    SafeDepositLockerEntity rentLocker(Long customerId, String lockerSize);
    List<SafeDepositLockerEntity> getAvailableLockers();
}
