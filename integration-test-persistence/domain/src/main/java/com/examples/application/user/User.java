package com.examples.application.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "t_users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phone;

    private Integer userStatus;

}
