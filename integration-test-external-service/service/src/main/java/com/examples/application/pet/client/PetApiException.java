package com.examples.application.pet.client;

import lombok.Getter;
import org.springframework.web.client.HttpStatusCodeException;

public class PetApiException extends RuntimeException {

    enum ErrorCode {
        PET_EXISTS,
        PET_NOT_FOUND,
        OTHER_ERROR
    }

    @Getter
    private final Long petId;
    private final ErrorCode errorCode;

    PetApiException(HttpStatusCodeException cause, ErrorDto error, ErrorCode errorCode) {
        super(error.message(), cause);
        this.petId = error.petId();
        this.errorCode = errorCode;
    }

    public boolean doesPetExists() {
        return errorCode == ErrorCode.PET_EXISTS;
    }

    public boolean isPetNotFound() {
        return errorCode == ErrorCode.PET_NOT_FOUND;
    }
}
