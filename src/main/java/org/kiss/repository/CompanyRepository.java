package org.kiss.repository;

import org.kiss.model.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, String> {

    boolean existsByRegistrationNumber(String registrationNumber);
}
