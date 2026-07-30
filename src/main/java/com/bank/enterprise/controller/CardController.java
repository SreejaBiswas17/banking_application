package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.dto.CardDto;
import com.bank.enterprise.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Card Operations", description = "Endpoints for Debit & Credit Card Issuance and Controls")
public class CardController {

    private final CardService cardService;

    @PostMapping("/issue")
    @Operation(summary = "Issue a new Debit or Credit Card")
    public ResponseEntity<ApiResponse<CardDto.CardResponse>> issueCard(@Valid @RequestBody CardDto.CardIssueRequest request) {
        CardDto.CardResponse response = cardService.issueCard(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Card issued successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card details by ID")
    public ResponseEntity<ApiResponse<CardDto.CardResponse>> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cardService.getCardById(id)));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get all cards linked to account")
    public ResponseEntity<ApiResponse<List<CardDto.CardResponse>>> getCardsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(ApiResponse.success(cardService.getCardsByAccountId(accountId)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update card status (Active, Temp Block, Perm Block)")
    public ResponseEntity<ApiResponse<CardDto.CardResponse>> updateCardStatus(@PathVariable Long id, @RequestParam CardStatus status) {
        return ResponseEntity.ok(ApiResponse.success(cardService.updateCardStatus(id, status), "Card status updated"));
    }

    @PutMapping("/{id}/pin")
    @Operation(summary = "Change Card PIN")
    public ResponseEntity<ApiResponse<String>> changePin(@PathVariable Long id, @Valid @RequestBody CardDto.CardPinChangeRequest request) {
        cardService.changeCardPin(id, request);
        return ResponseEntity.ok(ApiResponse.success("Card PIN updated successfully"));
    }
}
