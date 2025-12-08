package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Карты", description = "Управление банковскими картами пользователя")

public class CardController {
    private final CardService cardService;
    private final UserService userService;

    @Operation(summary = "Получить карту по ID", description = "Возвращает информацию о конкретной карте пользователя")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Карта найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class))), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Нет доступа к карте"), @ApiResponse(responseCode = "404", description = "Карта не найдена")})
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@Parameter(hidden = true) final Authentication authentication, @Parameter(description = "ID карты", required = true, example = "1") @PathVariable @Min(1) Long cardId) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Card card = cardService.getCardAsOwner(cardId, user);
            return ResponseEntity.ok(CardResponse.of(card));
        });
    }

    @Operation(summary = "Заблокировать карту", description = "Блокирует карту пользователя")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Карта заблокирована"), @ApiResponse(responseCode = "400", description = "Неверный запрос"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Нет доступа к карте"), @ApiResponse(responseCode = "404", description = "Карта не найдена")})
    @PostMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(@Parameter(hidden = true) final Authentication authentication, @Parameter(description = "ID карты", required = true, example = "1") @PathVariable @Min(1) Long cardId) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Card card = cardService.blockCardAsOwner(cardId, user);
            return ResponseEntity.ok(CardResponse.of(card));
        });
    }

    @Operation(summary = "Получить все карты пользователя", description = "Возвращает список всех карт пользователя с пагинацией")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Успешно получен список карт"), @ApiResponse(responseCode = "401", description = "Не авторизован")})
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getCards(@Parameter(hidden = true) final Authentication authentication, @Parameter(description = "Параметры пагинации") final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return AuthenticationUtil.withAuthentication(authentication, userDetails -> {
            User user = userService.getUser(userDetails.getId());
            Page<Card> cards = cardService.getUserCards(user, pageable);
            return ResponseEntity.ok(cards.map(CardResponse::of));
        });
    }
}