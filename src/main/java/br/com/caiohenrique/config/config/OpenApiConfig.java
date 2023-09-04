package br.com.caiohenrique.config.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenApi() {

        return new OpenAPI().info(new Info().
                title("RESTful API by Caio Henrique with Spring Boot.").
                version("v1").
                description("My first contact with API REST and RESTful.").
                termsOfService("https://github.com/caiohrmm/RESTwithSpring").
                license(new License().
                        name("Apache 2.0").
                        url("https://github.com/caiohrmm/RESTwithSpring")));
    }

}
