package com.examples.application.pet;

import com.examples.application.TestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ImportTestcontainers(TestContainerConfiguration.class)
public class PetApiTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void whenFetchingExistingPet_ThenResponseResolved() {
        RequestEntity<?> request = RequestEntity
                .get(URI.create("/v1/pet/12345"))
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ResponseEntity<Map<String, Object>> response = testRestTemplate.exchange(
                request,
                new ParameterizedTypeReference<>() {});

        assertAll(
                () -> assertThat(response.getStatusCode().value()).isEqualTo(200),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().size()).isEqualTo(6),
                () -> assertThat(response.getBody().get("id")).isEqualTo(12345),
                () -> assertThat(response.getBody().get("name")).isEqualTo("dog"),
                () -> assertThat(response.getBody().get("status")).isEqualTo("pending"),
                () -> assertThat(response.getBody().get("category")).isEqualTo(null),
                () -> assertThat(response.getBody().get("tags")).isInstanceOfSatisfying(
                        List.class,
                        list -> assertThat(list.size()).isEqualTo(2)
                ),
                () -> assertThat(response.getBody().get("photoUrls")).isEqualTo(
                        List.of(
                                "http://my.cdm.com/pet/12345/1",
                                "http://my.cdm.com/pet/12345/2",
                                "http://my.cdm.com/pet/12345/3"
                        )
                )
        );
    }
}
