package com.examples.application.pet;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(components = {
        PetApiRepository.class
},
        properties = {
            "demo.pet.client.basePath=http://dummy.org/v1/pet",
            "demo.pet.client.apiKey=THIS_IS_SECRET"
})
@ContextConfiguration(classes = {
        PetApiClientConfiguration.class,
        PetService.class
})
public class PetServiceTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockRestServiceServer mockServer;

    @Autowired
    PetService petService;

    @Test
    void whenFetchingExistingPet_ThenResponseResolved() throws JsonProcessingException {
        long petId = 1234L;
        String petName = "testName";
        String petStatus = "ON_STOCK";
        createMockBase(HttpMethod.GET, String.valueOf(petId))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new PetDto(petId, petName, petStatus))));
        Pet pet = petService.findPet(petId);
        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(pet.id()).isEqualTo(petId),
                () -> assertThat(pet.name()).isEqualTo(petName),
                () -> assertThat(pet.status()).isEqualTo(Pet.Status.AVAILABLE)
        );
    }

    @Test
    void whenFetchingNonExistingPet_ThenNoSuchElementExceptionThrown() {
        long petId = 1234L;
        createMockBase(HttpMethod.GET, String.valueOf(petId)).andRespond(withResourceNotFound());

        assertThatThrownBy(() -> petService.findPet(petId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Pet not found: " + petId);

        mockServer.verify();
    }

    @Test
    void givenPetDoesNotExists_whenCreatingPet_ThenPetCreated() throws JsonProcessingException {
        String petName = "testPet";
        PetDto petDto = new PetDto(null, petName, null) ;

        createMockBase(HttpMethod.POST, null)
                .andExpect(content().json(objectMapper.writeValueAsString(petDto)))
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(new PetDto(1234L, petName, "ORDERED")))
                );

        Pet pet = petService.create(new Pet(null, petName, null));
        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(pet.name()).isEqualTo(petName),
                () -> assertThat(pet.status()).isEqualTo(Pet.Status.PENDING),
                () -> assertThat(pet.id()).isEqualTo(1234L)
        );
    }

    @Test
    void givenPetExists_whenCreatingPet_ThenExistingPetReturned() throws JsonProcessingException {
        String petName = "testPet";
        long petId = 1234L;
        String petStatus = "ON_STOCK";

        PetDto petDto = new PetDto(null, petName, null);
        createMockBase(HttpMethod.POST, null)
                .andExpect(content().json(objectMapper.writeValueAsString(petDto)))
                .andRespond(
                        withStatus(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(
                                        new ErrorDto("resource exists", petId))
                                )
                );

        createMockBase(HttpMethod.GET, String.valueOf(petId))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(new PetDto(petId, petName, petStatus))));

        Pet pet = petService.create(new Pet(null, petName, null));

        assertAll(
                () -> mockServer.verify(),
                () -> assertThat(pet.name()).isEqualTo(petName),
                () -> assertThat(pet.status()).isEqualTo(Pet.Status.AVAILABLE),
                () -> assertThat(pet.id()).isEqualTo(petId)
        );
    }

    @Test
    void whenDeletingPet_ThenPetDeleted() {
        long petId = 1234L;
        createMockBase(HttpMethod.DELETE, String.valueOf(petId))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        petService.delete(petId);

        mockServer.verify();
    }

    @Test
    void whenSearchingByStatus_thenPetsWithSameStatusReturned() throws JsonProcessingException {

        List<PetDto> petDtos = List.of(
                new PetDto(1234L, "testName1", "ON_STOCK"),
                new PetDto(5678L, "testName2", "ON_STOCK")
        );

        createMockBase(HttpMethod.GET, "search?status=ON_STOCK")
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(petDtos))
                );

        List<Pet> petsByStatus = petService.findPetsByStatus(Pet.Status.AVAILABLE);
        assertAll(
                () -> assertThat(petsByStatus.size()).isEqualTo(2),
                () -> assertThat(petsByStatus).allMatch(pet -> pet.status().equals(Pet.Status.AVAILABLE)),
                () -> mockServer.verify()
        );
    }

    @Test
    void whenSearchingByTags_thenPetsTagsReturned() throws JsonProcessingException {

        List<PetDto> petDtos = List.of(
                new PetDto(1234L, "dog", "ON_STOCK"),
                new PetDto(5678L, "cat", "ORDERED")
        );

        createMockBase(HttpMethod.GET, "search?tags=dogs%2Bcats")
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(petDtos))
                );

        List<Pet> petsByStatus = petService.findByTags(List.of("dogs", "cats"));
        assertAll(
                () -> assertThat(petsByStatus.size()).isEqualTo(2),
                () -> mockServer.verify()
        );
    }


    private ResponseActions createMockBase(HttpMethod method,
                                           String path) {
        ResponseActions actions = mockServer
                .expect(requestTo("http://dummy.org/v1/pet/" + (path == null ? "" : path)))
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
