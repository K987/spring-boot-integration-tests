package com.examples.application.pet.client;

import java.util.List;

record PetDto(
        Long id,
        String name,
        String status,
        List<String> tags
) {}
