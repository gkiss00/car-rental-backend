package org.kiss.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.kiss.api.model.CarPage;
import org.kiss.api.model.CarRegistrationRequest;
import org.kiss.api.model.CarResponse;
import org.kiss.api.model.CarUpdateRequest;
import org.kiss.exception.CarAlreadyRegisteredException;
import org.kiss.exception.CarNotFoundException;
import org.kiss.exception.CompanyIdRequiredException;
import org.kiss.exception.CompanyNotFoundException;
import org.kiss.exception.InvalidReservationPeriodException;
import org.kiss.exception.UnknownCarTypeException;
import org.kiss.mapper.CarMapper;
import org.kiss.model.entity.Car;
import org.kiss.model.entity.Reservation;
import org.kiss.repository.CarRepository;
import org.kiss.repository.CarSearchCriteria;
import org.kiss.repository.CarTypeRepository;
import org.kiss.repository.CompanyRepository;
import org.kiss.repository.ReservationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final Duration AVAILABILITY_BUFFER = Duration.ofHours(2);

    private final CarRepository carRepository;
    private final CompanyRepository companyRepository;
    private final CarTypeRepository carTypeRepository;
    private final ReservationRepository reservationRepository;
    private final CarMapper carMapper;
    private final CurrentUserService currentUserService;

    public CarResponse registerCar(CarRegistrationRequest request) {
        if (!carTypeRepository.existsByMakeAndModel(request.getMake(), request.getModel())) {
            throw new UnknownCarTypeException(request.getMake(), request.getModel());
        }

        if (StringUtils.hasText(request.getVin()) && carRepository.existsByVin(request.getVin())) {
            throw new CarAlreadyRegisteredException(request.getVin());
        }

        String companyId = currentUserService.isAdmin()
                ? resolveRequestedCompanyId(request.getCompanyId())
                : currentUserService.getCallerCompany().getId();

        Car car = carMapper.toEntity(request, companyId);
        carRepository.save(car);

        return carMapper.toResponse(car);
    }

    public CarPage searchCars(CarSearchCriteria criteria) {
        List<String> excludedCarIds = resolveCarsUnavailableForPeriod(criteria.availableFrom(), criteria.availableUntil());
        return carMapper.toPage(carRepository.search(criteria, excludedCarIds));
    }

    public CarResponse getCarById(String id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
        return carMapper.toResponse(car);
    }

    public CarResponse updateCar(String id, CarUpdateRequest request) {
        Car existing = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));

        if (!currentUserService.isAdmin()) {
            requireOwnedByCallerCompany(existing);
        }

        if (StringUtils.hasText(request.getVin())
                && !request.getVin().equals(existing.getVin())
                && carRepository.existsByVin(request.getVin())) {
            throw new CarAlreadyRegisteredException(request.getVin());
        }

        Car updated = carMapper.applyUpdate(existing, request);
        carRepository.save(updated);

        return carMapper.toResponse(updated);
    }

    public CarPage listCarsForCurrentDealership(int page, int size) {
        String companyId = currentUserService.getCallerCompany().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return carMapper.toPage(carRepository.findByCompanyId(companyId, pageable));
    }

    public void deleteCar(String id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));

        if (!currentUserService.isAdmin()) {
            requireOwnedByCallerCompany(car);
        }

        carRepository.deleteById(id);
    }

    private List<String> resolveCarsUnavailableForPeriod(Instant availableFrom, Instant availableUntil) {
        if (availableFrom == null && availableUntil == null) {
            return List.of();
        }
        if (availableFrom == null || availableUntil == null) {
            throw new InvalidReservationPeriodException("availableFrom and availableUntil must be provided together");
        }
        if (!availableUntil.isAfter(availableFrom)) {
            throw new InvalidReservationPeriodException("availableUntil must be after availableFrom");
        }

        Instant bufferedWindowStart = availableFrom.minus(AVAILABILITY_BUFFER);
        Instant bufferedWindowEnd = availableUntil.plus(AVAILABILITY_BUFFER);

        return reservationRepository
                .findByStartTimeLessThanAndEndTimeGreaterThan(bufferedWindowEnd, bufferedWindowStart)
                .stream()
                .map(Reservation::getCarId)
                .distinct()
                .toList();
    }

    private void requireOwnedByCallerCompany(Car car) {
        String callerCompanyId = currentUserService.getCallerCompany().getId();
        if (!callerCompanyId.equals(car.getCompanyId())) {
            throw new AccessDeniedException("You do not own this car listing");
        }
    }

    private String resolveRequestedCompanyId(String companyId) {
        if (!StringUtils.hasText(companyId)) {
            throw new CompanyIdRequiredException("when registering a car as an admin");
        }
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException(companyId);
        }
        return companyId;
    }
}
