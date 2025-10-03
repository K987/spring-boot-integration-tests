package com.examples.application.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.examples.application.api.v1.UserApi;
import com.examples.application.api.v1.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UserV1Controller implements UserApi {

    private final UserService userService;

	@Override
	public ResponseEntity<UserDto> createUser(UserDto userDto) {
        return ResponseEntity.ok(fromUser(userService.create(toUser(userDto))));
	}

    @Override
	public ResponseEntity<UserDto> createUsersWithListInput(List<@Valid UserDto> userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("userDto is null");
        }
        List<User> users = userDto.stream().map(this::toUser).toList();
        users = userService.create(users);
        return ResponseEntity.ok(users.stream().map(this::fromUser).findFirst().get());
    }

	@Override
	public ResponseEntity<Void> deleteUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }
        userService.delete(username);
        return ResponseEntity.ok().build();
	}


	@Override
	public ResponseEntity<UserDto> getUserByName(String username) {
        return ResponseEntity.ok(fromUser(userService.fetchUserByUserName(username)));
	}

	@Override
	public ResponseEntity<String> loginUser(String username, String password) {
        userService.login(username, password);
        return ResponseEntity.ok()
                .header("X-Rate-Limit", "200")
                .header("X-Expires-After", LocalDateTime.now().plusHours(1).toString())
                .body(UUID.randomUUID().toString());
    }

	@Override
	public ResponseEntity<Void> logoutUser() {
        return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateUser(String username, UserDto userDto)  {
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
