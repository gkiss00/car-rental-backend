package org.kiss.repository;

import org.kiss.model.entity.Insurance;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InsuranceRepository extends MongoRepository<Insurance, String> {

    boolean existsByName(String name);
}
