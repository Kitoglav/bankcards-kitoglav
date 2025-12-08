package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.admin.CreateCardRequest;
import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.service.CardService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(final @RequestBody @Validated CreateCardRequest request) {
        Card card = cardService.createNewCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CardResponse.of(card));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(final @PathVariable @Min(1) Long cardId) {
        Card card = cardService.getCard(cardId);
        return ResponseEntity.ok(CardResponse.of(card));
    }

    @GetMapping
    public ResponseEntity<Page<CardResponse>> getAllCards(final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Card> cards = cardService.getCards(pageable);
        return ResponseEntity.ok(cards.map(CardResponse::of));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<CardResponse> deleteCard(final @PathVariable @Min(1) Long cardId) {
        try {
            Card card = cardService.deleteCard(cardId);
            return ResponseEntity.ok(CardResponse.of(card));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/activate/{cardId}")
    public ResponseEntity<CardResponse> activateCard(final @PathVariable @Min(1) Long cardId) {
        Card card = cardService.activateCard(cardId);
        return ResponseEntity.ok(CardResponse.of(card));
    }
    @PostMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(final @PathVariable @Min(1) Long cardId) {
        Card card = cardService.blockCard(cardId);
        return ResponseEntity.ok(CardResponse.of(card));
    }
}
