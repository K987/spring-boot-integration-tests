package com.examples.application.user;

import com.examples.application.config.ApiClientConfiguration;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.UserApi;
import org.openapitools.client.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@Import(ApiClientConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserApiTest {

    @Autowired
    ApiClient apiClient;

    UserApi userApi;

    @BeforeEach
    void setUp() {
        userApi = apiClient.user();
    }

    @Test
    @Order(1)
    void createUserTest() {
        UserDto dto = new UserDto()
                .id(100L)
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .userStatus(1)
                .email("test@test.com")
                .password("pwd");

        UserDto response = userApi.createUser()
                .body(dto)
                .executeAs(resp -> resp.then()
                        .contentType(ContentType.JSON)
                        .statusCode(HttpStatus.SC_OK)
                        .extract().response());

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(1L),
                () -> assertThat(response.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(response.getUsername()).isEqualTo("username")
                //...
        );
    }

    @Test
    @Order(2)
    void queryExistingUserTest() {

        UserDto response = userApi.getUserByName()
                .usernamePath("username")
                .executeAs(resp -> resp.then()
                        .contentType(ContentType.JSON)
                        .statusCode(HttpStatus.SC_OK)
                        .extract().response());

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(1L),
                () -> assertThat(response.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(response.getUsername()).isEqualTo("username")
                //...
        );
    }

    @Test
    @Order(3)
    void queryNonExistingUserTest() {

        userApi.getUserByName()
                .usernamePath("notUsername")
                .execute(resp -> resp.then()
                        .statusCode(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Order(4)
    void alterUserTest() {
        UserDto dto = new UserDto()
                .id(100L)
                .username("username-changed")
                .firstName("firstName-changed")
                .lastName("lastName-changed")
                .userStatus(1)
                .email("test@test.com")
                .password("pwd");

        userApi.updateUser()
                .usernamePath("username")
                .body(dto)
                .execute(resp -> resp.then()
                        .statusCode(HttpStatus.SC_NO_CONTENT));

        UserDto response = userApi.getUserByName()
                .usernamePath("username-changed")
                .executeAs(resp -> resp.then()
                        .contentType(ContentType.JSON)
                        .statusCode(HttpStatus.SC_OK)
                        .extract().response());

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(1L),
                () -> assertThat(response.getEmail()).isEqualTo("test@test.com"),
                () -> assertThat(response.getUsername()).isEqualTo("username-changed")
                //...
        );
    }

    @Test
    @Order(5)
    void deleteUserTest() {
        userApi.deleteUser()
                .usernamePath("username-changed")
                .execute(resp -> resp.then()
                        .statusCode(HttpStatus.SC_OK));
    }
}