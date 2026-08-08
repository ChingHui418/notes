package com.dev.notes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                                    .title("備忘錄系統 API 說明書")
                                    .version("1.0.0")
                                    .description("這是一份專為前端開發設計的 RestFul API 規格文件，包含筆記完整的 CRUD 功能。")
        );
    }
}
