package com.examples.application.pet.client;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
interface PetApiRepository {

    @GetExchange(value = "/{petId}")
    PetDto findPet(@PathVariable Long petId);

    @PostExchange(value = "/",
            contentType = MediaType.APPLICATION_JSON_VALUE)
    PetDto create(@RequestBody PetDto pet);

    @DeleteExchange("/{petId}")
    void delete(@PathVariable Long petId);

    @GetExchange(value = "/search")
    List<PetDto> search(@RequestParam MultiValueMap<String, String> params);

    @PatchExchange(value = "/{petId}",
            contentType = MediaType.APPLICATION_JSON_VALUE)
    PetDto update(@PathVariable Long petId, @RequestBody PetDto petDto);
}
