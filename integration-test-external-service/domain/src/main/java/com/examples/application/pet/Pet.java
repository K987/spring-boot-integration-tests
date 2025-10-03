package com.examples.application.pet;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Pet {

    public enum Status {
        AVAILABLE,
        PENDING,
        SOLD
    }

    Long id;
    String name;
    Status status;
    List<String> tags;
    List<String> photoUrls;
}
