package com.example.bankcards.config;
import com.example.bankcards.util.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private static final String API_PATTERN = "/api/**";

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(corsProperties.allowedCredentials());
        configuration.setAllowedOrigins(Arrays.asList(corsProperties.origins()));
        configuration.setAllowedMethods(Arrays.asList(corsProperties.methods()));
        configuration.setAllowedHeaders(Arrays.asList(corsProperties.headers()));
        configuration.setMaxAge(corsProperties.cache().maxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(API_PATTERN, configuration);
        return source;
    }

}
