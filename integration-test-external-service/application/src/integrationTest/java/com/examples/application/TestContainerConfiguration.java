package com.examples.application;

import com.examples.application.pet.PetApiTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.wiremock.integrations.testcontainers.WireMockContainer.WIREMOCK_2_LATEST;

public interface TestContainerConfiguration {

    @Container
    WireMockContainer wiremockServer =
            new WireMockContainer(WIREMOCK_2_LATEST)
                    .withMappingFromResource(PetApiTest.class, "wiremock-config.json");

    @DynamicPropertySource
    static void wiremockProperties(DynamicPropertyRegistry registry) {
        registry.add("demo.pet.client.host", TestContainerConfiguration.wiremockServer::getBaseUrl);
    }
}
