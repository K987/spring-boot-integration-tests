package com.examples.application.pet;

import java.util.List;

import com.examples.application.api.v1.ApiResponseDto;
import com.examples.application.api.v1.PetApi;
import com.examples.application.api.v1.PetDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PetV1Controller implements PetApi {

	@Override
	public ResponseEntity<PetDto> addPet(PetDto petDto) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<Void> deletePet(Long petId, String apiKey) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<List<PetDto>> findPetsByStatus(String status) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<List<PetDto>> findPetsByTags(List<String> tags) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<PetDto> getPetById(Long petId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<PetDto> updatePet(PetDto petDto) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<PetDto> updatePetWithForm(Long petId, String name, String status) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<ApiResponseDto> uploadFile(Long petId, String additionalMetadata, Resource body) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}
}
