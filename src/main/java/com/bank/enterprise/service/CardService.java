package com.bank.enterprise.service;

import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.dto.CardDto;

import java.util.List;

public interface CardService {
    CardDto.CardResponse issueCard(CardDto.CardIssueRequest request);
    CardDto.CardResponse getCardById(Long cardId);
    List<CardDto.CardResponse> getCardsByAccountId(Long accountId);
    CardDto.CardResponse updateCardStatus(Long cardId, CardStatus newStatus);
    void changeCardPin(Long cardId, CardDto.CardPinChangeRequest request);
}
