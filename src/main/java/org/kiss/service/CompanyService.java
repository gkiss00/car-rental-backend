package org.kiss.service;

import java.util.ArrayList;
import java.util.List;

import org.kiss.api.model.CompanyPage;
import org.kiss.api.model.CompanyRegistrationRequest;
import org.kiss.api.model.CompanyResponse;
import org.kiss.api.model.CompanyUpdateRequest;
import org.kiss.exception.CompanyAlreadyRegisteredException;
import org.kiss.exception.CompanyNotFoundException;
import org.kiss.exception.InvalidCompanyUserException;
import org.kiss.exception.UserNotFoundException;
import org.kiss.mapper.CompanyMapper;
import org.kiss.model.entity.Company;
import org.kiss.model.entity.util.Role;
import org.kiss.model.entity.User;
import org.kiss.repository.CompanyRepository;
import org.kiss.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;

    public CompanyResponse createCompany(CompanyRegistrationRequest request) {
        if (StringUtils.hasText(request.getRegistrationNumber())
                && companyRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new CompanyAlreadyRegisteredException(request.getRegistrationNumber());
        }

        validateUserIds(request.getUserIds());

        Company company = companyMapper.toEntity(request);
        companyRepository.save(company);

        return toResponseWithUsers(company);
    }

    public CompanyPage listCompanies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return companyMapper.toPage(companyRepository.findAll(pageable));
    }

    public CompanyResponse getCompanyById(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        return toResponseWithUsers(company);
    }

    public CompanyResponse updateCompany(String id, CompanyUpdateRequest request) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        if (StringUtils.hasText(request.getRegistrationNumber())
                && !request.getRegistrationNumber().equals(existing.getRegistrationNumber())
                && companyRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new CompanyAlreadyRegisteredException(request.getRegistrationNumber());
        }

        validateUserIds(request.getUserIds());

        Company updated = companyMapper.applyUpdate(existing, request);
        companyRepository.save(updated);

        return toResponseWithUsers(updated);
    }

    public CompanyResponse addUserToCompany(String companyId, String userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() == Role.ADMIN) {
            throw new InvalidCompanyUserException(userId, "is an admin account and cannot be attached to a company");
        }

        String previousCompanyId = user.getCompanyId();
        if (previousCompanyId != null && !previousCompanyId.equals(companyId)) {
            companyRepository.findById(previousCompanyId).ifPresent(previousCompany -> {
                List<String> previousUserIds = new ArrayList<>(
                        previousCompany.getUserIds() == null ? List.of() : previousCompany.getUserIds());
                previousUserIds.remove(userId);
                previousCompany.setUserIds(previousUserIds);
                companyRepository.save(previousCompany);
            });
        }

        List<String> userIds = new ArrayList<>(company.getUserIds() == null ? List.of() : company.getUserIds());
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
        company.setUserIds(userIds);
        companyRepository.save(company);

        user.setCompanyId(companyId);
        userRepository.save(user);

        return toResponseWithUsers(company);
    }

    public CompanyResponse removeUserFromCompany(String companyId, String userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<String> userIds = new ArrayList<>(company.getUserIds() == null ? List.of() : company.getUserIds());
        if (userIds.remove(userId)) {
            company.setUserIds(userIds);
            companyRepository.save(company);
        }

        if (companyId.equals(user.getCompanyId())) {
            user.setCompanyId(null);
            userRepository.save(user);
        }

        return toResponseWithUsers(company);
    }

    private CompanyResponse toResponseWithUsers(Company company) {
        List<String> userIds = company.getUserIds() == null ? List.of() : company.getUserIds();
        List<User> users = new ArrayList<>();
        userRepository.findAllById(userIds).forEach(users::add);

        return companyMapper.toResponse(company, users);
    }

    private void validateUserIds(List<String> userIds) {
        if (userIds == null) {
            return;
        }
        for (String userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new InvalidCompanyUserException(userId);
            }
        }
    }
}
