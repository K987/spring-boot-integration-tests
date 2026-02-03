package com.examples.application.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.examples.application.api.v1.UserApi;
import com.examples.application.api.v1.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
class UserV1Controller implements UserApi {

    private final UserService userService;

	@Override
	public ResponseEntity<UserDto> createUser(UserDto userDto) throws Exception {
        return ResponseEntity.ok(fromUser(userService.create(toUser(userDto))));
	}

    @Override
	public ResponseEntity<UserDto> createUsersWithListInput(List<@Valid UserDto> userDto) throws Exception {
        if (userDto == null || userDto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty user list");
        }
        List<User> users = userDto.stream().map(this::toUser).toList();
        users = userService.create(users);
        return ResponseEntity.ok(users.stream().map(this::fromUser).findFirst().get());
    }

	@Override
	public ResponseEntity<Void> deleteUser(String username) throws Exception {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is null");
        }
        userService.delete(username);
        return ResponseEntity.ok().build();
	}


	@Override
	public ResponseEntity<UserDto> getUserByName(String username) throws Exception {
        User user = userService.fetchUserByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return ResponseEntity.ok(fromUser(user));
	}

	@Override
	public ResponseEntity<String> loginUser(String username, String password) throws Exception {
        userService.login(username, password);
        return ResponseEntity.ok()
                .header("X-Rate-Limit", "200")
                .header("X-Expires-After", LocalDateTime.now().plusHours(1).toString())
                .body(UUID.randomUUID().toString());
    }

	@Override
	public ResponseEntity<Void> logoutUser() throws Exception {
        return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateUser(String username, UserDto userDto) throws Exception {
        userService.updateUser(username, toUser(userDto));
        return ResponseEntity.noContent().build();
	}

    private User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUserStatus(userDto.getUserStatus());
        return user;
    }

    private UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUserStatus(user.getUserStatus());
        return userDto;
    }
}
