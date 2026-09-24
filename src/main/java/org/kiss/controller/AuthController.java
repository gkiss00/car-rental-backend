package org.kiss.controller;

import org.kiss.api.AuthApi;
import org.kiss.api.model.AuthResponse;
import org.kiss.api.model.SigninRequest;
import org.kiss.api.model.SignupRequest;
import org.kiss.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> signup(SignupRequest signupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequest));
    }

    @Override
    public ResponseEntity<AuthResponse> signin(SigninRequest signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }
}
