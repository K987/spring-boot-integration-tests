package com.examples.application.pet.client;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URL;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Configuration
@EnableConfigurationProperties(PetWarehouseApiClientConfiguration.PetWarehouseApiClientConfigurationProperties.class)
class PetWarehouseApiClientConfiguration {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @ConfigurationProperties(prefix = "demo.pet.client")
    @Validated
    record PetWarehouseApiClientConfigurationProperties(
            @NotNull URL basePath,
            @NotNull String apiKey,
            String clientId
    ) {}

    @Bean
    @ConditionalOnProperty(value = "demo.pet.client.client-id")
    PetWarehouseRepository petApiRepositoryWithOauth(
            RestClient.Builder builder,
            PetWarehouseApiClientConfigurationProperties properties,
            OAuth2AuthorizedClientManager authorizedClientManager,
            OAuth2AuthorizedClientService authorizedClientService
    ) {

        OAuth2ClientHttpRequestInterceptor requestInterceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);

        OAuth2AuthorizationFailureHandler authorizationFailureHandler =
                OAuth2ClientHttpRequestInterceptor.authorizationFailureHandler(authorizedClientService);
        requestInterceptor.setAuthorizationFailureHandler(authorizationFailureHandler);

        RestClient client = createRestClientBase(builder, properties)
                .defaultRequest(req ->
                    req.attributes(clientRegistrationId(properties.clientId))
                )
                .requestInterceptor(requestInterceptor)
                .build();

        return createRepository(client);
    }

    @Bean
    @ConditionalOnProperty(value = "demo.pet.client.client-id", matchIfMissing = true, havingValue = "true")
    PetWarehouseRepository petApiRepository(
            RestClient.Builder builder,
            PetWarehouseApiClientConfigurationProperties properties
    ) {
        return createRepository(createRestClientBase(builder, properties).build());
    }

    private static RestClient.Builder createRestClientBase(RestClient.Builder builder, PetWarehouseApiClientConfigurationProperties properties) {
        return builder
                .baseUrl(properties.basePath.toString())
                .defaultHeaders(httpHeaders ->
                        httpHeaders.add(API_KEY_HEADER, properties.apiKey)
                );
    }

    private static PetWarehouseRepository createRepository(RestClient client) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(
                        RestClientAdapter.create(client)
                ).build();
        return factory.createClient(PetWarehouseRepository.class);
    }
}
