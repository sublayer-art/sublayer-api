package com.sublayer.api.config;

import com.sublayer.api.constants.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("Sublayer API Documentation")
                        .description("Sublayer NFT")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(Constants.WEB_TOKEN_NAME))
                .components(new Components().addSecuritySchemes(Constants.WEB_TOKEN_NAME,new SecurityScheme()
                        .name(Constants.WEB_TOKEN_NAME).type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }
}
