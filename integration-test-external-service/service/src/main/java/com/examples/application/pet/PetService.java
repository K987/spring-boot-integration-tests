package com.examples.application.pet;

import com.examples.application.pet.client.PetWarehouseApiClient;
import com.examples.application.pet.client.PetApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetWarehouseApiClient client;
    private final PetPhotoRepository photoStore;

    public Pet findPet(Long petId) {
        Pet pet = client.findPet(petId);
        return populatePhotos(pet);
    }

    public Pet create(Pet pet) {
        try {
            return client.create(
                    Pet.builder()
                            .name(pet.getName())
                            .tags(pet.getTags())
                            .build()
            );
        } catch (PetApiException e) {
            if (e.doesPetExists()) {
                return findPet(e.getPetId());
            }
            throw e;
        }
    }

    public void delete(Long petId) {
        client.delete(petId);
    }

    public List<Pet> findPetsByStatus(Pet.Status status) {
        List<Pet> result = client.search(status, null);
        return populatePhotos(result);
    }

    public List<Pet> findByTags(List<String> tags) {
        List<Pet> result = client.search(null, tags);
        return populatePhotos(result);
    }

    public Pet updatePet(Long petId, Pet pet) {
        Pet result = client.update(petId, pet);
        return populatePhotos(result);
    }

    private Pet populatePhotos(Pet pet) {
        pet.setPhotoUrls(photoStore.fetchPhotos(pet.getId()));
        return pet;
    }

    private List<Pet> populatePhotos(List<Pet> pet) {
        pet.forEach(this::populatePhotos);
        return pet;
    }
}
