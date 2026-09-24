package org.kiss.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.kiss.model.entity.Car;
import org.kiss.model.entity.util.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CarRepositoryImpl implements CarRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Car> search(CarSearchCriteria criteria, List<String> excludedCarIds) {
        List<Criteria> filters = new ArrayList<>();
        filters.add(Criteria.where("status").is(CarStatus.AVAILABLE));

        if (excludedCarIds != null && !excludedCarIds.isEmpty()) {
            filters.add(Criteria.where("id").nin(excludedCarIds));
        }

        if (StringUtils.hasText(criteria.companyId())) {
            filters.add(Criteria.where("companyId").is(criteria.companyId()));
        }
        if (StringUtils.hasText(criteria.make())) {
            filters.add(Criteria.where("make").regex(Pattern.quote(criteria.make()), "i"));
        }
        if (StringUtils.hasText(criteria.model())) {
            filters.add(Criteria.where("model").regex(Pattern.quote(criteria.model()), "i"));
        }
        if (StringUtils.hasText(criteria.category())) {
            filters.add(Criteria.where("category").is(criteria.category()));
        }
        if (StringUtils.hasText(criteria.transmission())) {
            filters.add(Criteria.where("transmission").is(criteria.transmission()));
        }
        if (StringUtils.hasText(criteria.fuelType())) {
            filters.add(Criteria.where("fuelType").is(criteria.fuelType()));
        }
        if (criteria.minPrice() != null || criteria.maxPrice() != null) {
            Criteria priceCriteria = Criteria.where("dailyPriceAmount");
            if (criteria.minPrice() != null) {
                priceCriteria = priceCriteria.gte(criteria.minPrice());
            }
            if (criteria.maxPrice() != null) {
                priceCriteria = priceCriteria.lte(criteria.maxPrice());
            }
            filters.add(priceCriteria);
        }
        if (StringUtils.hasText(criteria.city())) {
            filters.add(Criteria.where("location.city").regex("^" + Pattern.quote(criteria.city()) + "$", "i"));
        }
        if (StringUtils.hasText(criteria.country())) {
            filters.add(Criteria.where("location.country").regex("^" + Pattern.quote(criteria.country()) + "$", "i"));
        }

        Criteria combined = new Criteria().andOperator(filters.toArray(new Criteria[0]));
        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Car> cars = mongoTemplate.find(Query.query(combined).with(pageable), Car.class);
        long total = mongoTemplate.count(Query.query(combined), Car.class);

        return new PageImpl<>(cars, pageable, total);
    }
}
