package com.example.bankcards.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(security = @SecurityRequirement(name = "Bearer Authentication"), info = @Info(title = "BankCards API", version = "1.0.0", description = "REST API для управления банковскими картами", contact = @Contact(name = "Kitoglav", email = "vlad_lanayev@vk.com")))
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Введите JWT токен без префикса 'Bearer'"
)
public class OpenApiConfig {
}

