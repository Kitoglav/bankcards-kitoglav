package com.example.bankcards.util.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "application.cors")
public record CorsProperties(
        String[] origins,
        String[] methods,
        String[] headers,
        boolean allowedCredentials,
        Cache cache) {

    public record Cache(long maxAge) {
    }

}
