package com.examples.application.pet;

import java.util.List;

import com.examples.application.api.v1.ApiResponseDto;
import com.examples.application.api.v1.PetApi;
import com.examples.application.api.v1.PetDto;
import com.examples.application.api.v1.TagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class PetV1Controller implements PetApi {

    private final PetService petService;

	@Override
	public ResponseEntity<PetDto> addPet(PetDto petDto) {

        Pet pet = petService.create(toPet(petDto));
        return ResponseEntity.ok(fromPet(pet));
	}

    @Override
	public ResponseEntity<Void> deletePet(Long petId, String apiKey) {
        petService.delete(petId);
        return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<List<PetDto>> findPetsByStatus(String status) {
        List<PetDto> pets = petService.findPetsByStatus(toStatus(status)).stream()
                .map(this::fromPet)
                .toList();
        return ResponseEntity.ok(pets);
    }

    @Override
	public ResponseEntity<List<PetDto>> findPetsByTags(List<String> tags) {
        List<PetDto> pets = petService.findByTags(tags).stream()
                .map(this::fromPet)
                .toList();
        return ResponseEntity.ok(pets);
	}

	@Override
	public ResponseEntity<PetDto> getPetById(Long petId) {
        return ResponseEntity.ok(
                fromPet(petService.findPet(petId))
        );
	}

    @Override
	public ResponseEntity<PetDto> updatePet(PetDto petDto) {
        return ResponseEntity.ok(
                fromPet(
                        petService.updatePet(
                                petDto.getId(),
                                toPet(petDto)
                        )
                )
        );
    }

	@Override
	public ResponseEntity<PetDto> updatePetWithForm(Long petId, String name, String status) {
        PetDto petDto = new PetDto().id(petId).name(name).status(PetDto.StatusEnum.valueOf(status));
        return this.updatePet(petDto);
    }


    @Override
	public ResponseEntity<ApiResponseDto> uploadFile(Long petId, String additionalMetadata, Resource body) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

    private PetDto fromPet(Pet pet) {
        PetDto.StatusEnum status = switch (pet.getStatus()) {
            case AVAILABLE -> PetDto.StatusEnum.AVAILABLE;
            case PENDING -> PetDto.StatusEnum.PENDING;
            case SOLD -> PetDto.StatusEnum.SOLD;
        };
        List<TagDto> tags = pet
                .getTags()
                .stream().map(s -> new TagDto().name(s)).toList();

        return new PetDto()
                .id(pet.getId())
                .name(pet.getName())
                .status(status)
                .photoUrls(pet.getPhotoUrls())
                .tags(tags);
    }

    private Pet toPet(PetDto petDto) {
        return Pet.builder()
                .id(petDto.getId())
                .name(petDto.getName())
                .status(toStatus(petDto.getStatus()))
                .build();
    }

    private Pet.Status toStatus(String status) {
        if (status == null) {
            return null;
        }
        PetDto.StatusEnum statusEnum = PetDto.StatusEnum.fromValue(status);
        return toStatus(statusEnum);
    }
    private Pet.Status toStatus(PetDto.StatusEnum status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case AVAILABLE -> Pet.Status.AVAILABLE;
            case PENDING -> Pet.Status.PENDING;
            case SOLD -> Pet.Status.SOLD;
        };
    }

}
