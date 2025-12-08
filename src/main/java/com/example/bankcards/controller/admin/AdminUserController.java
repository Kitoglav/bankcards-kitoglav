package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.admin.CreateUserRequest;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.service.UserService;
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
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Администрирование пользователей", description = "API для администраторов по управлению пользователями")

public class AdminUserController {
    private final UserService userService;

    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя в системе")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Пользователь создан", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))), @ApiResponse(responseCode = "400", description = "Неверные данные пользователя"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "409", description = "Пользователь с таким именем уже существует")})
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Parameter(description = "Данные для создания пользователя", required = true) final @RequestBody @Validated CreateUserRequest request) {
        User user = userService.createNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(user));
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе по его идентификатору")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Пользователь найден"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@Parameter(description = "ID пользователя", required = true, example = "1") final @PathVariable @Min(1) Long userId) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(UserResponse.of(user));
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей в системе с пагинацией")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Успешно получен список пользователей"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав")})
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(@Parameter(description = "Параметры пагинации") final @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<User> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users.map(UserResponse::of));
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя из системы")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Пользователь удален"), @ApiResponse(responseCode = "204", description = "Пользователь не найден"), @ApiResponse(responseCode = "401", description = "Не авторизован"), @ApiResponse(responseCode = "403", description = "Недостаточно прав"), @ApiResponse(responseCode = "409", description = "У пользователя есть активные карты")})
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@Parameter(description = "ID пользователя", required = true, example = "1") final @PathVariable @Min(1) Long userId) {
        try {
            User user = userService.deleteUser(userId);
            return ResponseEntity.ok(UserResponse.of(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }
}