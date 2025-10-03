package com.examples.application.pet;


public record Pet(
   Long id,
   String name,
   Status status
) {
    public enum Status {
        AVAILABLE,
        PENDING,
        SOLD
    }
}
