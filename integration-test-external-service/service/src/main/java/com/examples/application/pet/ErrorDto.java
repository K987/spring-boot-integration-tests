package com.examples.application.pet;

public record ErrorDto(
        String message,
        long petId
) {}
