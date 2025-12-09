package com.examples.application.pet.client;


import com.examples.application.pet.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(
        properties = {
            "demo.pet.client.basePath=http://dummy.org/v1/pet",
            "demo.pet.client.apiKey=THIS_IS_SECRET"
})
@ContextConfiguration(classes = {
        PetWarehouseApiClientConfiguration.class,
        PetWarehouseRepository.class,
        PetWarehouseApiClient.class
})
class PetWarehouseApiClientTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    PetWarehouseApiClient warehouseApiClient;

    @Test
    void whenFetchingExistingPet_ThenResponseResolved() {
        long petId = 1234L;
        String petName = "testName";
        String petStatus = "ON_STOCK";
        List<String> tags = List.of("test-tag-1", "test-tag-2");
        createMockBase(HttpMethod.GET, String.valueOf(petId))
                .andRespond(withAccepted()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new PetDto(petId, petName, petStatus, tags))
                        )
                );
        Pet pet = warehouseApiClient.findPet(petId);
        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(pet.getId()).isEqualTo(petId),
                () -> assertThat(pet.getName()).isEqualTo(petName),
                () -> assertThat(pet.getStatus()).isEqualTo(Pet.Status.AVAILABLE),
                () -> assertThat(pet.getTags()).isEqualTo(tags)
        );
    }

    @Test
    void whenFetchingNonExistingPet_ThenPetApiExceptionThrown() {
        long petId = 1234L;
        createMockBase(HttpMethod.GET, String.valueOf(petId)).andRespond(
                withResourceNotFound()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new ErrorDto("Pet does not exist", petId)))
        );

        PetApiException petApiException = catchThrowableOfType(
                PetApiException.class,
                () -> warehouseApiClient.findPet(petId)
        );

        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(petApiException.getPetId()).isEqualTo(petId),
                () -> assertThat(petApiException.doesPetExists()).isFalse(),
                () -> assertThat(petApiException.isPetNotFound()).isTrue()
        );
    }

    @Test
    void givenPetDoesNotExists_whenCreatingPet_ThenPetCreated() {
        String petName = "testPet";
        List<String> tags = List.of("test-tag-1", "test-tag-2");
        PetDto petDto = new PetDto(null, petName, null, tags) ;

        createMockBase(HttpMethod.POST, null)
                .andExpect(content().json(objectMapper.writeValueAsString(petDto)))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(new PetDto(1234L, petName, "ORDERED", tags)))
                );

        Pet pet = warehouseApiClient.create(
                Pet.builder()
                        .name(petName)
                        .tags(tags)
                        .build()
        );
        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(pet.getName()).isEqualTo(petName),
                () -> assertThat(pet.getStatus()).isEqualTo(Pet.Status.PENDING),
                () -> assertThat(pet.getId()).isEqualTo(1234L),
                () -> assertThat(pet.getTags()).isEqualTo(tags)
        );
    }

    @Test
    void givenPetExists_whenCreatingPet_ThenPetApiExceptionThrown() {
        String petName = "testPet";
        long petId = 1234L;
        List<String> tags = List.of("test-tag-1", "test-tag-2");
        PetDto petDto = new PetDto(null, petName, null, tags);
        createMockBase(HttpMethod.POST, null)
                .andExpect(content().json(objectMapper.writeValueAsString(petDto)))
                .andRespond(
                        withRequestConflict()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(
                                        new ErrorDto("resource exists", petId))
                                )
                );

        PetApiException petApiException = catchThrowableOfType(
                PetApiException.class,
                () -> warehouseApiClient.create(
                        Pet.builder()
                                .name(petName)
                                .tags(tags)
                                .build())
        );

        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(petApiException.getPetId()).isEqualTo(1234L),
                () -> assertThat(petApiException.doesPetExists()).isTrue(),
                () -> assertThat(petApiException.isPetNotFound()).isFalse()
        );
    }

    @Test
    void whenDeletingPet_ThenPetDeleted() {
        long petId = 1234L;
        createMockBase(HttpMethod.DELETE, String.valueOf(petId))
                .andRespond(withNoContent());
        warehouseApiClient.delete(petId);

        mockServer.verify();
    }

    @Test
    void whenSearchingByStatus_thenPetsWithSameStatusReturned() {
        List<String> tags = List.of("test-tag-1", "test-tag-2");
        List<PetDto> petDtos = List.of(
                new PetDto(1234L, "testName1", "ON_STOCK", tags),
                new PetDto(5678L, "testName2", "ON_STOCK", tags)
        );

        createMockBase(HttpMethod.GET, "search?status=ON_STOCK")
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(petDtos))
                );

        List<Pet> petsByStatus = warehouseApiClient.search(Pet.Status.AVAILABLE, null);
        assertAll(
                () -> assertThat(petsByStatus).hasSize(2),
                () -> assertThat(petsByStatus).allMatch(pet -> pet.getStatus().equals(Pet.Status.AVAILABLE)),
        () -> mockServer.verify()
        );
    }

    @Test
    void whenSearchingByTags_thenPetsTagsReturned() {
        List<String> tags = List.of("test-tag-1", "test-tag-2");
        List<PetDto> petDtos = List.of(
                new PetDto(1234L, "dog", "ON_STOCK", tags),
                new PetDto(5678L, "cat", "ORDERED", tags)
        );

        createMockBase(HttpMethod.GET, "search?tags=test-tag-1&tags=test-tag-2")
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(petDtos))
                );

        List<Pet> petsByStatus = warehouseApiClient.search(null, tags);
        assertAll(
                () -> assertThat(petsByStatus).hasSize(2),
                () -> assertThat(petsByStatus).allMatch(pet -> pet.getTags().containsAll(tags)),
                () -> mockServer.verify()
        );
    }


    private ResponseActions createMockBase(HttpMethod method,
                                           String path) {
        return createMockBase(ExpectedCount.times(1), method, path);
    }

    private ResponseActions createMockBase(ExpectedCount count, HttpMethod method,
                                           String path) {
        ResponseActions actions = mockServer
                .expect(count, requestTo("http://dummy.org/v1/pet/" + (path == null ? "" : path)))
                .andExpect(header("X-API-KEY", "THIS_IS_SECRET"))
                .andExpect(method(method));
        if (!HttpMethod.DELETE.equals(method)) {
            actions.andExpect(header("Accept", "application/json"));
        }
        if (List.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).contains(method)) {
            actions.andExpect(header("Content-Type", "application/json"));
        }
        return actions;
    }
}
