package org.kiss.repository;

import java.util.List;

import org.kiss.model.entity.Car;
import org.springframework.data.domain.Page;

public interface CarRepositoryCustom {

    Page<Car> search(CarSearchCriteria criteria, List<String> excludedCarIds);
}
