package com.bank.enterprise.service;

public interface NotificationService {
    void sendNotification(Long userId, String subject, String message);
}
