package org.kiss.repository;

import org.kiss.model.entity.CarType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarTypeRepository extends MongoRepository<CarType, String> {

    boolean existsByMakeAndModel(String make, String model);
}
