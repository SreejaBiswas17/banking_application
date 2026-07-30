package com.bank.enterprise.repository;

import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user by username")
    void saveAndFindByUsername() {
        UserEntity user = UserEntity.builder()
                .username("test_user_repo")
                .passwordHash("hash123")
                .email("repo@test.com")
                .phoneNumber("12345")
                .userRole(UserRole.CUSTOMER)
                .isEnabled(true)
                .isLocked(false)
                .failedAttempts(0)
                .build();

        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findByUsername("test_user_repo");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("repo@test.com");
    }

    @Test
    @DisplayName("Should return true for existsByUsername when present")
    void existsByUsername() {
        UserEntity user = UserEntity.builder()
                .username("existing_user")
                .passwordHash("hash")
                .email("exist@test.com")
                .phoneNumber("9999")
                .userRole(UserRole.CUSTOMER)
                .build();

        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("existing_user");
        assertThat(exists).isTrue();
    }
}
