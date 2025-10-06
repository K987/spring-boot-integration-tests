package com.examples.application.pet.client;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URL;

@Configuration
@EnableConfigurationProperties(PetWarehouseApiClientConfiguration.PetWarehouseApiClientConfigurationProperties.class)
class PetWarehouseApiClientConfiguration {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @ConfigurationProperties(prefix = "demo.pet.client")
    @Validated
    record PetWarehouseApiClientConfigurationProperties(
            @NotNull URL basePath,
            @NotNull String apiKey
    ) {}

    @Bean
    PetWarehouseRepository petApiRepository(
            RestClient.Builder builder,
            PetWarehouseApiClientConfigurationProperties properties
    ) {

        RestClient client = builder
                .baseUrl(properties.basePath.toString())
                .defaultHeaders(httpHeaders ->
                    httpHeaders.add(API_KEY_HEADER, properties.apiKey)
                )
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(
                        RestClientAdapter.create(client)
                ).build();
        return factory.createClient(PetWarehouseRepository.class);
    }
}
