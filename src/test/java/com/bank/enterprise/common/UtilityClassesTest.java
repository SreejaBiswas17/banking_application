package com.bank.enterprise.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UtilityClassesTest {

    @Test
    @DisplayName("Should correctly hash strings using SHA256")
    void testHashSHA256() {
        String input = "SecretPassword123";
        String hash1 = EncryptionUtils.hashSHA256(input);
        String hash2 = EncryptionUtils.hashSHA256(input);

        assertThat(hash1).isNotNull();
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(input);
    }

    @Test
    @DisplayName("Should correctly mask card numbers")
    void testMaskCardNumber() {
        String card = "4532123456789012";
        String masked = EncryptionUtils.maskCardNumber(card);

        assertThat(masked).isEqualTo("4532-XXXX-XXXX-9012");
    }

    @Test
    @DisplayName("Should correctly calculate adulthood status")
    void testIsAdult() {
        LocalDate adultDob = LocalDate.now().minusYears(20);
        LocalDate minorDob = LocalDate.now().minusYears(15);

        assertThat(DateUtils.isAdult(adultDob)).isTrue();
        assertThat(DateUtils.isAdult(minorDob)).isFalse();
    }
}
