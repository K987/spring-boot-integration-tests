package com.examples.application.pet;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
class PetPhotoRepository {

    List<String> fetchPhotos(long petId) {
        return IntStream
                .of(1,2,3)
                .mapToObj(value -> String.format("http://my.cdm.com/pet/%s/%s", petId, value))
                .toList();
    }
}
