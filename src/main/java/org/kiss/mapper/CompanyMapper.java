package org.kiss.mapper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.kiss.api.model.CompanyPage;
import org.kiss.api.model.CompanyRegistrationRequest;
import org.kiss.api.model.CompanyResponse;
import org.kiss.api.model.CompanyStatus;
import org.kiss.api.model.CompanySummaryResponse;
import org.kiss.api.model.CompanyUpdateRequest;
import org.kiss.model.entity.Company;
import org.kiss.model.entity.util.CompanyAddress;
import org.kiss.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final UserMapper userMapper;

    public Company toEntity(CompanyRegistrationRequest request) {
        CompanyAddress address = toEntityAddress(request.getAddress());
        List<String> userIds = request.getUserIds() == null ? new ArrayList<>() : request.getUserIds();

        Instant now = Instant.now();

        return Company.builder()
                .userIds(userIds)
                .name(request.getName())
                .registrationNumber(request.getRegistrationNumber())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(address)
                .website(request.getWebsite())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .status(org.kiss.model.entity.util.CompanyStatus.PENDING_VERIFICATION)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Company applyUpdate(Company existing, CompanyUpdateRequest request) {
        if (request.getUserIds() != null) {
            existing.setUserIds(new ArrayList<>(request.getUserIds()));
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getRegistrationNumber() != null) {
            existing.setRegistrationNumber(request.getRegistrationNumber());
        }
        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            existing.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            existing.setAddress(toEntityAddress(request.getAddress()));
        }
        if (request.getWebsite() != null) {
            existing.setWebsite(request.getWebsite());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getLogoUrl() != null) {
            existing.setLogoUrl(request.getLogoUrl());
        }
        if (request.getStatus() != null) {
            existing.setStatus(org.kiss.model.entity.util.CompanyStatus.valueOf(request.getStatus().name()));
        }
        existing.setUpdatedAt(Instant.now());

        return existing;
    }

    public CompanyResponse toResponse(Company company, List<User> users) {
        return new CompanyResponse()
                .id(company.getId())
                .users(users.stream().map(userMapper::toProfileResponse).toList())
                .name(company.getName())
                .registrationNumber(company.getRegistrationNumber())
                .email(company.getEmail())
                .phoneNumber(company.getPhoneNumber())
                .address(toApiAddress(company.getAddress()))
                .website(company.getWebsite())
                .description(company.getDescription())
                .logoUrl(company.getLogoUrl())
                .status(CompanyStatus.valueOf(company.getStatus().name()))
                .createdAt(company.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(company.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }

    public CompanySummaryResponse toSummary(Company company) {
        CompanyAddress address = company.getAddress();

        return new CompanySummaryResponse()
                .id(company.getId())
                .name(company.getName())
                .city(address == null ? null : address.getCity())
                .country(address == null ? null : address.getCountry())
                .logoUrl(company.getLogoUrl())
                .status(CompanyStatus.valueOf(company.getStatus().name()))
                .createdAt(company.getCreatedAt().atOffset(ZoneOffset.UTC));
    }

    public CompanyPage toPage(Page<Company> page) {
        return new CompanyPage()
                .content(page.getContent().stream().map(this::toSummary).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }

    private CompanyAddress toEntityAddress(org.kiss.api.model.CompanyAddress apiAddress) {
        if (apiAddress == null) {
            return null;
        }
        return CompanyAddress.builder()
                .street(apiAddress.getStreet())
                .city(apiAddress.getCity())
                .postalCode(apiAddress.getPostalCode())
                .country(apiAddress.getCountry())
                .build();
    }

    private org.kiss.api.model.CompanyAddress toApiAddress(CompanyAddress address) {
        if (address == null) {
            return null;
        }
        return new org.kiss.api.model.CompanyAddress()
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry());
    }
}
