package com.bank.enterprise.repository;

import com.bank.enterprise.common.NotificationStatus;
import com.bank.enterprise.model.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUser_UserIdAndStatus(Long userId, NotificationStatus status);

    Page<NotificationEntity> findByUser_UserId(Long userId, Pageable pageable);
}
