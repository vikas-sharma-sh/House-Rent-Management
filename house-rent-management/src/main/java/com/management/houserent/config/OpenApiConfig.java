//package com.management.houserent.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenApiConfig {
//    @Bean
//    public OpenAPI houseRentOpenAPI() {
//        return new OpenAPI().info(new Info().title("House Rent API").version("v1"));
//    }
//}
package com.management.houserent.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI houseRentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("House Rent Management API")
                        .description("REST API for owners, tenants, rooms, leases, payments and bills")
                        .version("v1.0.0")
                        .contact(new Contact().name("Your Team").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://github.com/your-org/house-rent-management")); // update later
    }

    // Group for Owner endpoints
    @Bean
    public GroupedOpenApi ownersGroup() {
        return GroupedOpenApi.builder()
                .group("owners")
                .pathsToMatch("/api/owners/**")
                .build();
    }

    // in OpenApiConfig.java (add another bean)
    @Bean
    public org.springdoc.core.models.GroupedOpenApi tenantsGroup() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("tenants")
                .pathsToMatch("/api/tenants/**")
                .build();
    }


    // You can add more groups later:
    // tenantsGroup -> "/api/tenants/**"
    // roomsGroup -> "/api/rooms/**"
}

