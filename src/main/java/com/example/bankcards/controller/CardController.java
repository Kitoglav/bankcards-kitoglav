package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.AuthenticationUtil;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final UserService userService;

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(final Authentication authentication, @PathVariable @Min(1) Long cardId) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Card card = cardService.getCardAsOwner(cardId, user);
            return ResponseEntity.ok(CardResponse.of(card));
        });
    }
    @PostMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(final Authentication authentication, @PathVariable @Min(1) Long cardId) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Card card = cardService.blockCardAsOwner(cardId, user);
            return ResponseEntity.ok(CardResponse.of(card));
        });
    }
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getCards(final Authentication authentication, final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
           User user = userService.getUser(userDetails.getId());
           Page<Card> cards = cardService.getUserCards(user, pageable);
           return ResponseEntity.ok(cards.map(CardResponse::of));
        });
    }
}
