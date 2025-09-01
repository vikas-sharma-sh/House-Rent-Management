package com.management.houserent.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI houseRentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("House Rent Management API")
                        .description("REST API for managing owners, tenants, rooms, leases, and admin tasks")
                        .version("v2.0.0")
                        .contact(new Contact().name("HouseRent Team").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth", List.of("read","write")));
    }

    // Group: Owners
    @Bean
    public GroupedOpenApi ownersGroup() {
        return GroupedOpenApi.builder()
                .group("owners")
                .pathsToMatch("/api/owners/**")
                .build();
    }

    // Group: Tenants
    @Bean
    public GroupedOpenApi tenantsGroup() {
        return GroupedOpenApi.builder()
                .group("tenants")
                .pathsToMatch("/api/tenants/**")
                .build();
    }

    // Group: Rooms
    @Bean
    public GroupedOpenApi roomsGroup() {
        return GroupedOpenApi.builder()
                .group("rooms")
                .pathsToMatch("/api/rooms/**")
                .build();
    }

    // Group: Admin
    @Bean
    public GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    // Group: Auth (login/register)
    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }
}
