package com.examples.application.pet;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetApiRepository client;

    public Pet findPet(Long petId) {
        try {
            return toPet(client.findPet(petId));
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("Pet not found: " + petId);
        }
    }

    public Pet create(Pet pet) {
        Pet result;
        try {
            result = toPet(client.create(new PetDto(null, pet.name(), null)));
        } catch (HttpClientErrorException.Conflict e) {
            ErrorDto error = e.getResponseBodyAs(ErrorDto.class);
            result = findPet(error.petId());
        }
        return result;
    }

    public void delete(Long petId) {
        client.delete(petId);
    }

    public List<Pet> findPetsByStatus(Pet.Status status) {
        MultiValueMap<String, String> params = PetApiRepository.searchParamBuilder()
                .setStatus(fromStatus(status))
                .build();
        return client.search(params).stream()
                .map(this::toPet)
                .toList();
    }


    public List<Pet> findByTags(List<String> tags) {
        PetApiRepository.SearchParamBuilder searchParamBuilder = PetApiRepository.searchParamBuilder();
        tags.forEach(searchParamBuilder::addTag);

        return client.search(searchParamBuilder.build()).stream()
                .map(this::toPet)
                .toList();
    }


    public Pet updatePet(Long petId, String name) {
       return toPet(
               client.update(petId, new PetDto(petId, name, null))
       );
    }


    private String fromStatus(Pet.Status status) {
        return switch (status) {
            case AVAILABLE -> "ON_STOCK";
            case PENDING -> "ORDERED";
            case SOLD -> "OUT_OF_STOCK";
        };
    }

    private Pet toPet(PetDto pet) {
        Pet.Status status = switch (pet.status()) {
            case "ON_STOCK" -> Pet.Status.AVAILABLE;
            case "OUT_OF_STOCK" -> Pet.Status.SOLD;
            case "ORDERED" -> Pet.Status.PENDING;
            default -> throw new IllegalStateException("Unexpected value: " + pet.status());
        };
        return new Pet(pet.id(), pet.name(), status);
    }

}
