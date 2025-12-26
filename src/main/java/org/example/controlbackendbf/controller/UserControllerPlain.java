package org.example.controlbackendbf.controller;

import lombok.RequiredArgsConstructor;
import org.example.controlbackendbf.api.UserApi;
import org.example.controlbackendbf.api.model.CreateUserRequest;
import org.example.controlbackendbf.api.model.User;
import org.example.controlbackendbf.services.UserService;
import org.mapstruct.control.MappingControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerPlain implements UserApi {
    private final UserService userService;

    @Override
    public ResponseEntity<List<User>> listUsers() {

        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    public ResponseEntity<User> getUserById(Long id) {

        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    public ResponseEntity<User> createUser(CreateUserRequest body) {
        User created = userService.create(body);

            return ResponseEntity.created(URI.create("/users/"+created.getId())).body(created);
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<User> updateUserById(Long id, CreateUserRequest body) {
       User updated = userService.updateUserById(id, body);
        return ResponseEntity.ok().body(updated);
    }
}
