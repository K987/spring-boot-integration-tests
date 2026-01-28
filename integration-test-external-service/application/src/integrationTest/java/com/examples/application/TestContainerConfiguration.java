package com.examples.application;

import com.examples.application.pet.PetApiTest;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.wiremock.integrations.testcontainers.WireMockContainer.OFFICIAL_IMAGE_NAME;

public interface TestContainerConfiguration {

    @Container
    WireMockContainer wiremockServer =
            new WireMockContainer(OFFICIAL_IMAGE_NAME + ":3.5.4")
                    .withMappingFromResource(PetApiTest.class, "wiremock-config.json");

    @Container
    KeycloakContainer keyCloakContainer =
            new KeycloakContainer() //quay.io/keycloak/keycloak:25.0
                    .withRealmImportFile("keycloak/realm.json");

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registerDatabaseHost(registry);
        registerAuthServer(registry);
    }

    private static void registerDatabaseHost(DynamicPropertyRegistry registry) {
        registry.add("demo.pet.client.host", TestContainerConfiguration.wiremockServer::getBaseUrl);
    }

    private static void registerAuthServer(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.default.issuer-uri",
                () -> TestContainerConfiguration.keyCloakContainer.getAuthServerUrl() + "/realms/spring-integration-test");
    }
}
