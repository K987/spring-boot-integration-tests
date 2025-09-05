package com.examples.application.user;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.examples.application.api.v1.UserApi;
import com.examples.application.api.v1.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserV1Controller implements UserApi {


	@Override
	public ResponseEntity<UserDto> createUser(UserDto userDto) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<UserDto> createUsersWithListInput(List<@Valid UserDto> userDto) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<Void> deleteUser(String username) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public ResponseEntity<UserDto> getUserByName(String username) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<String> loginUser(String username, String password) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<Void> logoutUser() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<Void> updateUser(String username, UserDto userDto) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}
}
