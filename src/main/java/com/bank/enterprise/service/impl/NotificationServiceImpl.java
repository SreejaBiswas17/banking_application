package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.NotificationStatus;
import com.bank.enterprise.common.NotificationType;
import com.bank.enterprise.model.NotificationEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.NotificationRepository;
import com.bank.enterprise.repository.UserRepository;
import com.bank.enterprise.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Async
    @Transactional
    public void sendNotification(Long userId, String subject, String message) {
        userRepository.findById(userId).ifPresent(user -> {
            NotificationEntity notification = NotificationEntity.builder()
                    .user(user)
                    .notificationType(NotificationType.EMAIL)
                    .subject(subject)
                    .message(message)
                    .status(NotificationStatus.SENT)
                    .build();
            notificationRepository.save(notification);
        });
    }
}
