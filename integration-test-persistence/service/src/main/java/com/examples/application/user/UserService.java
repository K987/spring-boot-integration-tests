package com.examples.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        if (Objects.nonNull(user.getId()) && userRepository.existsById(user.getId())) {
            throw new IllegalStateException("User with id " + user.getId() + " already exists");
        }
        user.setId(null);
        return userRepository.save(user);
    }


    public List<User> create(List<User> users) {
        return users.stream().map(this::create).toList();
    }

    public void delete(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public User fetchUserByUserName(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void login(String username, String password) {
        User user = userRepository.findUserByUsernameAndPassword(username, password);
        if (user == null) {
            throw new IllegalStateException("Invalid username or password");
        }
    }

    public void updateUser(String username, User user) {
        User toUpdate = this.fetchUserByUserName(username);
        if (toUpdate == null) {
            throw new IllegalStateException("User does not exist");
        }
        toUpdate.setUsername(user.getUsername());
        toUpdate.setPassword(user.getPassword());
        toUpdate.setEmail(user.getEmail());
        toUpdate.setFirstName(user.getFirstName());
        toUpdate.setLastName(user.getLastName());
        toUpdate.setUserStatus(user.getUserStatus());
    }
}
