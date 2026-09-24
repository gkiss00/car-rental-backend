package org.kiss.service;

import org.kiss.api.model.AuthResponse;
import org.kiss.api.model.SigninRequest;
import org.kiss.api.model.SignupRequest;
import org.kiss.exception.EmailAlreadyInUseException;
import org.kiss.mapper.UserMapper;
import org.kiss.model.entity.User;
import org.kiss.repository.UserRepository;
import org.kiss.security.JwtProperties;
import org.kiss.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException(request.getEmail());
        }

        User user = userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse signin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return userMapper.toAuthResponse(user, token, jwtProperties.expirationMs() / 1000);
    }
}
