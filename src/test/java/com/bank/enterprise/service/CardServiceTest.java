package com.bank.enterprise.service;

import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.common.CardType;
import com.bank.enterprise.dto.CardDto;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CardEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CardRepository;
import com.bank.enterprise.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CardServiceImpl cardService;

    private AccountEntity account;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder().userId(1L).username("carduser").build();
        CustomerEntity customer = CustomerEntity.builder().customerId(10L).user(user).build();
        account = AccountEntity.builder().accountId(100L).accountNumber("1234567890").customer(customer).build();
    }

    @Test
    @DisplayName("Should successfully issue a new card")
    void issueCard_Success() {
        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pin");

        CardEntity card = CardEntity.builder()
                .cardId(1L)
                .cardNumberMasked("4532-XXXX-XXXX-1234")
                .account(account)
                .cardType(CardType.DEBIT_GOLD)
                .expiryDate(LocalDate.now().plusYears(4))
                .cardStatus(CardStatus.ACTIVE)
                .build();

        when(cardRepository.save(any(CardEntity.class))).thenReturn(card);

        CardDto.CardIssueRequest req = CardDto.CardIssueRequest.builder()
                .accountId(100L)
                .cardType(CardType.DEBIT_GOLD)
                .pin("1234")
                .build();

        CardDto.CardResponse res = cardService.issueCard(req);

        assertThat(res).isNotNull();
        assertThat(res.getCardType()).isEqualTo(CardType.DEBIT_GOLD);
        assertThat(res.getCardStatus()).isEqualTo(CardStatus.ACTIVE);
    }
}
