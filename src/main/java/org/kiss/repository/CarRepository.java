package org.kiss.repository;

import java.util.List;

import org.kiss.model.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<Car, String>, CarRepositoryCustom {

    boolean existsByVin(String vin);

    Page<Car> findByCompanyId(String companyId, Pageable pageable);

    List<Car> findByCompanyId(String companyId);
}
