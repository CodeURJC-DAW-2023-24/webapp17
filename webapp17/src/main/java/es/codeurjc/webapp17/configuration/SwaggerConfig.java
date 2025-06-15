package es.codeurjc.webapp17.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("El Ricón del Software APIREST")
                        .description("API documentation for El Ricón del Software web application")
                        .version("1.0.13"));
    }
}