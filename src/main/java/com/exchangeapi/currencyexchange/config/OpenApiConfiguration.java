package com.exchangeapi.currencyexchange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Exchange API")
                        .version("1.0")
                        .description("""
                                This is an api which provides currency exchange
                                for one hour according to currency names
                                """
                        ));
    }

}
