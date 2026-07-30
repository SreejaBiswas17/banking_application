package com.bank.enterprise.repository;

import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserEntity> findByUserRole(UserRole userRole);

    @Modifying
    @Query("UPDATE UserEntity u SET u.failedAttempts = u.failedAttempts + 1 WHERE u.username = :username")
    int incrementFailedAttempts(@Param("username") String username);

    @Modifying
    @Query("UPDATE UserEntity u SET u.failedAttempts = 0 WHERE u.username = :username")
    int resetFailedAttempts(@Param("username") String username);

    @Modifying
    @Query("UPDATE UserEntity u SET u.isLocked = :locked WHERE u.username = :username")
    int updateLockStatus(@Param("username") String username, @Param("locked") boolean locked);
}
