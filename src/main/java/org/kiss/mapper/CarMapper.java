package org.kiss.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

import org.kiss.api.model.CarPage;
import org.kiss.api.model.CarRegistrationRequest;
import org.kiss.api.model.CarResponse;
import org.kiss.api.model.CarStatus;
import org.kiss.api.model.CarUpdateRequest;
import org.kiss.model.entity.Car;
import org.kiss.model.entity.util.CarCategory;
import org.kiss.model.entity.util.CarLocation;
import org.kiss.model.entity.util.FuelType;
import org.kiss.model.entity.util.TransmissionType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CarMapper {

    private static final String DEFAULT_CURRENCY = "EUR";

    public Car toEntity(CarRegistrationRequest request, String companyId) {
        var apiLocation = request.getLocation();
        CarLocation location = CarLocation.builder()
                .city(apiLocation.getCity())
                .country(apiLocation.getCountry())
                .build();

        Instant now = Instant.now();

        return Car.builder()
                .companyId(companyId)
                .vin(request.getVin())
                .licensePlate(request.getLicensePlate())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .category(CarCategory.valueOf(request.getCategory().name()))
                .transmission(TransmissionType.valueOf(request.getTransmission().name()))
                .fuelType(FuelType.valueOf(request.getFuelType().name()))
                .seats(request.getSeats())
                .doors(request.getDoors())
                .color(request.getColor())
                .mileageKm(request.getMileageKm())
                .dailyPriceAmount(BigDecimal.valueOf(request.getDailyPriceAmount()))
                .dailyPriceCurrency(StringUtils.hasText(request.getDailyPriceCurrency())
                        ? request.getDailyPriceCurrency()
                        : DEFAULT_CURRENCY)
                .status(org.kiss.model.entity.util.CarStatus.AVAILABLE)
                .location(location)
                .features(request.getFeatures())
                .imageUrls(request.getImageUrls())
                .description(request.getDescription())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Car applyUpdate(Car existing, CarUpdateRequest request) {
        var apiLocation = request.getLocation();

        existing.setVin(request.getVin());
        existing.setLicensePlate(request.getLicensePlate());
        existing.setMake(request.getMake());
        existing.setModel(request.getModel());
        existing.setYear(request.getYear());
        existing.setCategory(CarCategory.valueOf(request.getCategory().name()));
        existing.setTransmission(TransmissionType.valueOf(request.getTransmission().name()));
        existing.setFuelType(FuelType.valueOf(request.getFuelType().name()));
        existing.setSeats(request.getSeats());
        existing.setDoors(request.getDoors());
        existing.setColor(request.getColor());
        existing.setMileageKm(request.getMileageKm());
        existing.setDailyPriceAmount(BigDecimal.valueOf(request.getDailyPriceAmount()));
        existing.setDailyPriceCurrency(StringUtils.hasText(request.getDailyPriceCurrency())
                ? request.getDailyPriceCurrency()
                : DEFAULT_CURRENCY);
        existing.setLocation(CarLocation.builder()
                .city(apiLocation.getCity())
                .country(apiLocation.getCountry())
                .build());
        existing.setFeatures(request.getFeatures());
        existing.setImageUrls(request.getImageUrls());
        existing.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            existing.setStatus(org.kiss.model.entity.util.CarStatus.valueOf(request.getStatus().name()));
        }
        existing.setUpdatedAt(Instant.now());

        return existing;
    }

    public CarResponse toResponse(Car car) {
        var location = new org.kiss.api.model.CarLocation()
                .city(car.getLocation().getCity())
                .country(car.getLocation().getCountry());

        return new CarResponse()
                .id(car.getId())
                .companyId(car.getCompanyId())
                .vin(car.getVin())
                .licensePlate(car.getLicensePlate())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .category(org.kiss.api.model.CarCategory.valueOf(car.getCategory().name()))
                .transmission(org.kiss.api.model.TransmissionType.valueOf(car.getTransmission().name()))
                .fuelType(org.kiss.api.model.FuelType.valueOf(car.getFuelType().name()))
                .seats(car.getSeats())
                .doors(car.getDoors())
                .color(car.getColor())
                .mileageKm(car.getMileageKm())
                .dailyPriceAmount(car.getDailyPriceAmount().doubleValue())
                .dailyPriceCurrency(car.getDailyPriceCurrency())
                .status(CarStatus.valueOf(car.getStatus().name()))
                .location(location)
                .features(car.getFeatures())
                .imageUrls(car.getImageUrls())
                .description(car.getDescription())
                .createdAt(car.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(car.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }

    public CarPage toPage(Page<Car> page) {
        return new CarPage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
