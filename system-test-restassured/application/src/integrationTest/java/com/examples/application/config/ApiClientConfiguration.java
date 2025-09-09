package com.examples.application.config;


import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.openapitools.client.JacksonObjectMapper.jackson;

import io.restassured.builder.RequestSpecBuilder;
import org.openapitools.client.ApiClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@TestConfiguration(proxyBeanMethods = false)
public class ApiClientConfiguration {

    @Bean
    @Lazy
    ApiClient apiClient(@LocalServerPort String port) {
        return ApiClient
                .api(ApiClient.Config
                        .apiConfig()
                        .reqSpecSupplier(() -> new RequestSpecBuilder()
                                .setBaseUri("http://localhost:" + port)
                                .setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(jackson())))
                        )
                );
    }
}
