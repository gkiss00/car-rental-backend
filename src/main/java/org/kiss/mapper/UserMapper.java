package org.kiss.mapper;

import java.time.Instant;

import org.kiss.api.model.AuthResponse;
import org.kiss.api.model.CreateUserRequest;
import org.kiss.api.model.UserPage;
import org.kiss.api.model.UserProfileResponse;
import org.kiss.api.model.UserRole;
import org.kiss.api.model.SignupRequest;
import org.kiss.model.entity.util.Role;
import org.kiss.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(SignupRequest request, String passwordHash) {
        return User.builder()
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.valueOf(request.getRole().name()))
                .createdAt(Instant.now())
                .build();
    }

    public User toEntity(CreateUserRequest request, String passwordHash) {
        return User.builder()
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.valueOf(request.getRole().name()))
                .createdAt(Instant.now())
                .build();
    }

    public AuthResponse toAuthResponse(User user, String accessToken, long expiresInSeconds) {
        return new AuthResponse()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresInSeconds)
                .email(user.getEmail())
                .role(UserRole.valueOf(user.getRole().name()));
    }

    public UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(UserRole.valueOf(user.getRole().name()))
                .companyId(user.getCompanyId());
    }

    public UserPage toPage(Page<User> page) {
        return new UserPage()
                .content(page.getContent().stream().map(this::toProfileResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
