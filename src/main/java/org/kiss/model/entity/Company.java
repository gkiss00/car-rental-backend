package org.kiss.model.entity;

import java.time.Instant;
import java.util.List;

import org.kiss.model.entity.util.CompanyAddress;
import org.kiss.model.entity.util.CompanyStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    private String id;

    private List<String> userIds;

    private String name;

    @Indexed(unique = true, sparse = true)
    private String registrationNumber;

    private String email;
    private String phoneNumber;
    private CompanyAddress address;
    private String website;
    private String description;
    private String logoUrl;

    private CompanyStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}
