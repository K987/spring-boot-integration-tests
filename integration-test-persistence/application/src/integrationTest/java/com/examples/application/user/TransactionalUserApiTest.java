package com.examples.application.user;

import com.examples.application.api.v1.UserDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

import static org.instancio.Select.field;

@SpringBootTest
@AutoConfigureRestTestClient
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionalUserApiTest {

    @Autowired
    RestTestClient restTestClient;
    private static final String ALICE_DOE = "alice_doe";

    @Test
    @Order(1)
    void saveUserInTransaction() {
        UserDto userDto = Instancio.of(UserDto.class)
                .ignore(field("id"))
                .set(field("username"), ALICE_DOE)
                .create();

        restTestClient
                .post()
                .uri("/v1/user")
                .body(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo(ALICE_DOE);
    }

    @Test
    @Order(2)
    void fetchUserInAnotherTransaction() {
        restTestClient
                .get()
                .uri("/v1/user/{username}", ALICE_DOE)
                .exchange()
                .expectStatus().isNotFound();
    }
}

