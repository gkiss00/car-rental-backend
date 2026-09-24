package org.kiss.service;

import java.util.ArrayList;
import java.util.List;

import org.kiss.api.model.ChangePasswordRequest;
import org.kiss.api.model.CreateUserRequest;
import org.kiss.api.model.UserPage;
import org.kiss.api.model.UserProfileResponse;
import org.kiss.api.model.UserRole;
import org.kiss.exception.CompanyIdRequiredException;
import org.kiss.exception.CompanyNotFoundException;
import org.kiss.exception.EmailAlreadyInUseException;
import org.kiss.exception.InvalidCurrentPasswordException;
import org.kiss.exception.PasswordConfirmationMismatchException;
import org.kiss.mapper.UserMapper;
import org.kiss.model.entity.Company;
import org.kiss.model.entity.User;
import org.kiss.repository.CompanyRepository;
import org.kiss.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CurrentUserService currentUserService;

    public UserProfileResponse getCurrentUserProfile() {
        return userMapper.toProfileResponse(currentUserService.getCallerUser());
    }

    public UserProfileResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException(request.getEmail());
        }

        boolean callerIsAdmin = currentUserService.isAdmin();
        UserRole role = request.getRole();

        if (role == UserRole.ADMIN && !callerIsAdmin) {
            throw new AccessDeniedException("Only admins can create admin accounts");
        }

        Company company = null;
        if (role == UserRole.USER || role == UserRole.DEALERSHIP) {
            company = callerIsAdmin ? resolveRequestedCompany(request.getCompanyId()) : currentUserService.getCallerCompany();
        }

        User user = userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        user.setCompanyId(company != null ? company.getId() : null);
        userRepository.save(user);

        if (company != null) {
            addUserToCompany(company, user.getId());
        }

        return userMapper.toProfileResponse(user);
    }

    public UserPage listUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (StringUtils.hasText(search)) {
            return userMapper.toPage(userRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            search, search, search, pageable));
        }

        return userMapper.toPage(userRepository.findAll(pageable));
    }

    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
            throw new PasswordConfirmationMismatchException();
        }

        User user = currentUserService.getCallerUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private Company resolveRequestedCompany(String companyId) {
        if (!StringUtils.hasText(companyId)) {
            throw new CompanyIdRequiredException();
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    private void addUserToCompany(Company company, String userId) {
        List<String> userIds = new ArrayList<>(
                company.getUserIds() == null ? List.of() : company.getUserIds());
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
        company.setUserIds(userIds);

        companyRepository.save(company);
    }
}
