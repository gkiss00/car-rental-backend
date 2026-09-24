package org.kiss.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.kiss.api.model.CreateReservationRequest;
import org.kiss.api.model.ReservationPage;
import org.kiss.api.model.ReservationResponse;
import org.kiss.exception.CarNotFoundException;
import org.kiss.exception.CompanyNotFoundException;
import org.kiss.exception.InvalidReservationPeriodException;
import org.kiss.exception.ReservationConflictException;
import org.kiss.exception.ReservationNotFoundException;
import org.kiss.mapper.ReservationMapper;
import org.kiss.model.entity.Car;
import org.kiss.model.entity.Reservation;
import org.kiss.repository.CarRepository;
import org.kiss.repository.CompanyRepository;
import org.kiss.repository.ReservationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Duration MIN_GAP = Duration.ofHours(2);

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final CompanyRepository companyRepository;
    private final ReservationMapper reservationMapper;
    private final CurrentUserService currentUserService;

    public ReservationResponse createReservation(CreateReservationRequest request) {
        requireCarAccessible(request.getCarId());

        Instant startTime = request.getStartTime().toInstant();
        Instant endTime = request.getEndTime().toInstant();

        if (!endTime.isAfter(startTime)) {
            throw new InvalidReservationPeriodException();
        }

        requireNoConflict(request.getCarId(), startTime, endTime, null);

        Reservation reservation = reservationMapper.toEntity(request);
        reservationRepository.save(reservation);

        return reservationMapper.toResponse(reservation);
    }

    public ReservationPage listReservationsForCar(String carId, int page, int size) {
        requireCarAccessible(carId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));
        return reservationMapper.toPage(reservationRepository.findByCarId(carId, pageable));
    }

    public ReservationResponse getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        requireCarAccessible(reservation.getCarId());

        return reservationMapper.toResponse(reservation);
    }

    public ReservationResponse updateReservation(String id, CreateReservationRequest request) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        requireCarAccessible(existing.getCarId());
        requireCarAccessible(request.getCarId());

        Instant startTime = request.getStartTime().toInstant();
        Instant endTime = request.getEndTime().toInstant();

        if (!endTime.isAfter(startTime)) {
            throw new InvalidReservationPeriodException();
        }

        requireNoConflict(request.getCarId(), startTime, endTime, id);

        Reservation updated = reservationMapper.applyUpdate(existing, request);
        reservationRepository.save(updated);

        return reservationMapper.toResponse(updated);
    }

    public ReservationPage listReservationsForCompany(String companyId, int page, int size) {
        requireCompanyAccessible(companyId);

        List<String> carIds = carRepository.findByCompanyId(companyId).stream().map(Car::getId).toList();
        Pageable pageable = PageRequest.of(page, size,          Sort.by(Sort.Direction.ASC, "startTime"));

        return reservationMapper.toPage(reservationRepository.findByCarIdIn(carIds, pageable));
    }

    public void deleteReservation(String id) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        requireCarAccessible(existing.getCarId());

        reservationRepository.deleteById(id);
    }

    private Car requireCarAccessible(String carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new CarNotFoundException(carId));

        if (!currentUserService.isAdmin()) {
            String callerCompanyId = currentUserService.getCallerCompany().getId();
            if (!callerCompanyId.equals(car.getCompanyId())) {
                throw new AccessDeniedException("This car does not belong to your company");
            }
        }

        return car;
    }

    private void requireCompanyAccessible(String companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException(companyId);
        }

        if (!currentUserService.isAdmin()) {
            String callerCompanyId = currentUserService.getCallerCompany().getId();
            if (!callerCompanyId.equals(companyId)) {
                throw new AccessDeniedException("This company is not your own");
            }
        }
    }

    private void requireNoConflict(String carId, Instant startTime, Instant endTime, String excludeReservationId) {
        for (Reservation existing : reservationRepository.findByCarId(carId)) {
            if (existing.getId().equals(excludeReservationId)) {
                continue;
            }

            boolean endsWithGapBeforeNewStart = !existing.getEndTime().plus(MIN_GAP).isAfter(startTime);
            boolean startsWithGapAfterNewEnd = !endTime.plus(MIN_GAP).isAfter(existing.getStartTime());

            if (!endsWithGapBeforeNewStart && !startsWithGapAfterNewEnd) {
                throw new ReservationConflictException(carId);
            }
        }
    }
}
