package org.kiss.controller;

import org.kiss.api.UsersApi;
import org.kiss.api.model.ChangePasswordRequest;
import org.kiss.api.model.CreateUserRequest;
import org.kiss.api.model.UserPage;
import org.kiss.api.model.UserProfileResponse;
import org.kiss.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALERSHIP')")
    public ResponseEntity<UserProfileResponse> createUser(CreateUserRequest createUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserRequest));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserPage> listUsers(Integer page, Integer size, String search) {
        return ResponseEntity.ok(userService.listUsers(page, size, search));
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
