package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.admin.CreateCardRequest;
import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.entity.impl.Card;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Администрирование карт", description = "API для администраторов по управлению картами")
public class AdminCardController {
    private final CardService cardService;

    @Operation(summary = "Создать новую карту", description = "Создает новую банковскую карту для пользователя")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Карта создана", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class))), @ApiResponse(responseCode = "400", description = "Неверные данные карты"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав")})
    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Parameter(description = "Данные для создания карты", required = true) final @RequestBody @Validated CreateCardRequest request) {
        Card card = cardService.createNewCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CardResponse.of(card));
    }

    @Operation(summary = "Получить карту по ID", description = "Возвращает информацию о карте по ее идентификатору")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Карта найдена"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "404", description = "Карта не найдена")})
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@Parameter(description = "ID карты", required = true, example = "1") final @PathVariable @Min(1) Long cardId) {
        Card card = cardService.getCard(cardId);
        return ResponseEntity.ok(CardResponse.of(card));
    }

    @Operation(summary = "Получить все карты", description = "Возвращает список всех карт в системе с пагинацией")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Успешно получен список карт"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав")})
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getAllCards(@Parameter(description = "Параметры пагинации") final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Card> cards = cardService.getCards(pageable);
        return ResponseEntity.ok(cards.map(CardResponse::of));
    }

    @Operation(summary = "Удалить карту", description = "Удаляет карту из системы")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Карта удалена"), @ApiResponse(responseCode = "204", description = "Карта не найдена"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав")})
    @DeleteMapping("/{cardId}")
    public ResponseEntity<CardResponse> deleteCard(@Parameter(description = "ID карты", required = true, example = "1") final @PathVariable @Min(1) Long cardId) {
        try {
            Card card = cardService.deleteCard(cardId);
            return ResponseEntity.ok(CardResponse.of(card));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Активировать карту", description = "Активирует заблокированную карту")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Карта активирована"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "404", description = "Карта не найдена")})
    @PostMapping("/activate/{cardId}")
    public ResponseEntity<CardResponse> activateCard(@Parameter(description = "ID карты", required = true, example = "1") final @PathVariable @Min(1) Long cardId) {
        Card card = cardService.activateCard(cardId);
        return ResponseEntity.ok(CardResponse.of(card));
    }

    @Operation(summary = "Заблокировать карту", description = "Блокирует карту в системе")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Карта заблокирована"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "404", description = "Карта не найдена")})
    @PostMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(@Parameter(description = "ID карты", required = true, example = "1") final @PathVariable @Min(1) Long cardId) {
        Card card = cardService.blockCard(cardId);
        return ResponseEntity.ok(CardResponse.of(card));
    }
}