package com.examples.application.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "${spring.flyway.placeholders.usersTableName}")
@Getter
@Setter
public class User {

    @Id
    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phone;

    private Integer userStatus;

}
