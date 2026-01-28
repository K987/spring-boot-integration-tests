package com.examples.application.pet;

import com.examples.application.TestContainerConfiguration;
import com.examples.application.WiremockHelper;
import com.examples.application.api.v1.PetDto;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureRestTestClient
@ImportTestcontainers(TestContainerConfiguration.class)
public class PetApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    void whenFetchingExistingPet_ThenResponseResolved() {
       restTestClient
               .get().uri("/v1/pet/12345")
               //.accept(MediaType.APPLICATION_JSON)
               .exchangeSuccessfully()
               .expectBody(PetDto.class)
               .value(petDto -> assertAll(
                       () -> assertThat(petDto.getId()).isEqualTo(12345L),
                       () -> assertThat(petDto.getName()).isEqualTo("dog"),
                       () -> assertThat(petDto.getStatus()).isEqualTo(PetDto.StatusEnum.PENDING),
                       () -> assertThat(petDto.getCategory()).isNull(),
                       () -> assertThat(petDto.getTags()).hasSize(2),
                       () -> assertThat(petDto.getTags()).anyMatch(tagDto -> "tag-1".equals(tagDto.getName())),
                       () -> assertThat(petDto.getTags()).anyMatch(tagDto -> "tag-2".equals(tagDto.getName())),
                       () -> assertThat(petDto.getPhotoUrls()).containsExactly(
                               "http://my.cdm.com/pet/12345/1",
                               "http://my.cdm.com/pet/12345/2",
                               "http://my.cdm.com/pet/12345/3"
                       )
               ));

        WireMock wireMockClient = WiremockHelper.getWireMockClient();

        wireMockClient.verifyThat(
                WireMock.getRequestedFor(
                        WireMock.urlEqualTo("/v1/pet/12345"))
                        .withHeader("Authorization", WireMock.matching("Bearer .*")
                        )
        );
    }
}
