package com.examples.application.pet.client;

import com.examples.application.pet.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PetWarehouseApiClient {

    private final PetWarehouseRepository repository;

    public Pet findPet(Long petId) {
        try {
            return toPet(repository.findPet(petId));
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.Gone e) {
            throw toException(e, PetApiException.ErrorCode.PET_NOT_FOUND);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid petId: " + petId, e);
        }
    }

    public Pet create(Pet pet) {
        try {
            return toPet(
                    repository.create(
                            new PetDto(null, pet.getName(), null, pet.getTags())
                    )
            );
        } catch (HttpClientErrorException.Conflict e) {
            throw toException(e, PetApiException.ErrorCode.PET_EXISTS);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid pet: " + pet, e);
        }
    }

    public void delete(Long petId) {
        repository.delete(petId);
    }

    public List<Pet> search(Pet.Status status, List<String> tags) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (status != null) {
            queryParams.put("status", List.of(fromStatus(status)));
        }
        if (!CollectionUtils.isEmpty(tags)) {
            queryParams.put("tags", tags);
        }

        List<PetDto> result = repository.search(queryParams);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        } else {
            return result.stream().map(this::toPet).toList();
        }
    }

    public Pet update(Long petId, Pet pet) {
        try {
            return toPet(
                    repository.update(
                            petId, new PetDto(petId, pet.getName(), null, pet.getTags())
                    )
            );
        } catch (HttpClientErrorException.Gone | HttpClientErrorException.NotFound e) {
            throw toException(e, PetApiException.ErrorCode.PET_NOT_FOUND);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Invalid pet: " + pet, e);
        }
    }

    private String fromStatus(Pet.Status status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case AVAILABLE -> "ON_STOCK";
            case PENDING -> "ORDERED";
            case SOLD -> "OUT_OF_STOCK";
        };
    }

    private PetApiException toException(HttpClientErrorException e, PetApiException.ErrorCode code) {
        ErrorDto error = e.getResponseBodyAs(ErrorDto.class);
        if (error == null) {
            error = new ErrorDto(e.getMessage(), null);
        }
        return new PetApiException(e, error, code);
    }

    private Pet toPet(PetDto pet) {
        Pet.Status status = switch (pet.status()) {
            case "ON_STOCK" -> Pet.Status.AVAILABLE;
            case "OUT_OF_STOCK" -> Pet.Status.SOLD;
            case "ORDERED" -> Pet.Status.PENDING;
            default -> throw new IllegalStateException("Unexpected value: " + pet.status());
        };
        return Pet.builder()
                .id(pet.id())
                .name(pet.name())
                .status(status)
                .tags(pet.tags())
                .build();
    }
}
