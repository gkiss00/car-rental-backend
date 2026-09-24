package org.kiss.repository;

import java.math.BigDecimal;
import java.time.Instant;

public record CarSearchCriteria(
        String companyId,
        String make,
        String model,
        String category,
        String transmission,
        String fuelType,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String city,
        String country,
        Instant availableFrom,
        Instant availableUntil,
        int page,
        int size) {
}
