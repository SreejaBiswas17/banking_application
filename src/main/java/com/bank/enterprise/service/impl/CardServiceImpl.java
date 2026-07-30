package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.common.EncryptionUtils;
import com.bank.enterprise.dto.CardDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.CardProcessingException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CardEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CardRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final Random random = new Random();

    @Override
    @Transactional
    public CardDto.CardResponse issueCard(CardDto.CardIssueRequest request) {
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));

        String rawCardNumber = generate16DigitCardNumber();
        String maskedNumber = EncryptionUtils.maskCardNumber(rawCardNumber);
        String cardHash = EncryptionUtils.hashSHA256(rawCardNumber);
        String pinHash = passwordEncoder.encode(request.getPin());

        CardEntity card = CardEntity.builder()
                .cardNumberMasked(maskedNumber)
                .cardHash(cardHash)
                .account(account)
                .cardType(request.getCardType())
                .expiryDate(LocalDate.now().plusYears(4))
                .cvvHash(passwordEncoder.encode(String.format("%03d", random.nextInt(1000))))
                .cardStatus(CardStatus.ACTIVE)
                .build();

        CardEntity savedCard = cardRepository.save(card);

        auditService.logAction("ISSUE_CARD", account.getCustomer().getUser().getUsername(), "CARD", savedCard.getCardId().toString(), null, "Card Issued: " + maskedNumber);

        return mapToCardResponse(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto.CardResponse getCardById(Long cardId) {
        CardEntity card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));
        return mapToCardResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDto.CardResponse> getCardsByAccountId(Long accountId) {
        return cardRepository.findByAccount_AccountId(accountId).stream()
                .map(this::mapToCardResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardDto.CardResponse updateCardStatus(Long cardId, CardStatus newStatus) {
        CardEntity card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));

        String oldStatus = card.getCardStatus().name();
        card.setCardStatus(newStatus);
        CardEntity updated = cardRepository.save(card);

        auditService.logAction("UPDATE_CARD_STATUS", "SYSTEM", "CARD", cardId.toString(), oldStatus, newStatus.name());

        return mapToCardResponse(updated);
    }

    @Override
    @Transactional
    public void changeCardPin(Long cardId, CardDto.CardPinChangeRequest request) {
        CardEntity card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "id", cardId));

        if (card.getCardStatus() != CardStatus.ACTIVE) {
            throw new CardProcessingException("Cannot change PIN for inactive or blocked card");
        }

        card.setCvvHash(passwordEncoder.encode(request.getNewPin()));
        cardRepository.save(card);

        auditService.logAction("CHANGE_CARD_PIN", card.getAccount().getCustomer().getUser().getUsername(), "CARD", cardId.toString(), null, "PIN updated");
    }

    private String generate16DigitCardNumber() {
        StringBuilder sb = new StringBuilder("4532"); // Visa prefix
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private CardDto.CardResponse mapToCardResponse(CardEntity entity) {
        return CardDto.CardResponse.builder()
                .cardId(entity.getCardId())
                .cardNumberMasked(entity.getCardNumberMasked())
                .accountNumber(entity.getAccount().getAccountNumber())
                .cardType(entity.getCardType())
                .expiryDate(entity.getExpiryDate())
                .cardStatus(entity.getCardStatus())
                .dailyAtmLimit(entity.getDailyAtmLimit())
                .dailyPosLimit(entity.getDailyPosLimit())
                .creditLimit(entity.getCreditLimit())
                .usedCredit(entity.getUsedCredit())
                .build();
    }
}
