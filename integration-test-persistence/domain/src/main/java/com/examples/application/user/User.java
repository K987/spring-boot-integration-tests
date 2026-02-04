package com.examples.application.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
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

    @PostPersist
    public void postPersist() {
        System.out.println("PostPersist called for User: " + this.username);
    }

}
