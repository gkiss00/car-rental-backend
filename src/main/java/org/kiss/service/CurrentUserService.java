package org.kiss.service;

import org.kiss.exception.CompanyNotFoundException;
import org.kiss.exception.DealershipWithoutCompanyException;
import org.kiss.model.entity.Company;
import org.kiss.model.entity.User;
import org.kiss.repository.CompanyRepository;
import org.kiss.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public User getCallerUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
    }

    public Company getCallerCompany() {
        User caller = getCallerUser();

        if (!StringUtils.hasText(caller.getCompanyId())) {
            throw new DealershipWithoutCompanyException();
        }

        return companyRepository.findById(caller.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(caller.getCompanyId()));
    }

    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ADMIN_AUTHORITY::equals);
    }
}
