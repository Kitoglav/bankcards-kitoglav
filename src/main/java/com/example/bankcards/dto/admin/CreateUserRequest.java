package com.example.bankcards.dto.admin;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateUserRequest(@NotNull @Length(min = 6, max = 32) String username,
                                @NotNull @Length(min = 6, max = 128) String password) {
}
