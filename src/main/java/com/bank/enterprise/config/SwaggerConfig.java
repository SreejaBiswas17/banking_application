package com.bank.enterprise.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bankingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Banking System REST API")
                        .description("Production grade banking core engine with Oracle DB persistence and JUnit 5 test coverage.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Enterprise Core Banking Engineering")
                                .email("engineering@bank.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }
}
