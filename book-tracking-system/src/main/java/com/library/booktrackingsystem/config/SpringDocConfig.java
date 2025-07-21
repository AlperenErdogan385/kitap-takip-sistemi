package com.library.booktrackingsystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Bu isim, Swagger UI'da görünecek güvenlik şemasının ismidir.

        return new OpenAPI()
                // API'nin buradaki güvenlik şemasını kullanacağını belirtir.
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Güvenlik şemasının tanımını ekler
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName) // Güvenlik şemasının adı
                                        .type(SecurityScheme.Type.HTTP) // HTTP tabanlı güvenlik
                                        .scheme("bearer") // "Bearer Token" şeması
                                        .bearerFormat("JWT"))) // Token formatı JWT
                .info(new Info().title("Kitap Takip Sistemi API").version("1.0").description(
                        "Kitap Takip Sistemi uygulaması için API dökümantasyonu."));
    }
}