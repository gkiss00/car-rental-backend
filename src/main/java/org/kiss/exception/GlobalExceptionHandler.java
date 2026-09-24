package org.kiss.exception;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.kiss.api.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(CarAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleCarAlreadyRegistered(CarAlreadyRegisteredException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarNotFound(CarNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(CompanyAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleCompanyAlreadyRegistered(CompanyAlreadyRegisteredException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidCompanyUserException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCompanyUser(InvalidCompanyUserException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(CompanyIdRequiredException.class)
    public ResponseEntity<ErrorResponse> handleCompanyIdRequired(CompanyIdRequiredException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFound(CompanyNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(CarTypeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCarTypeAlreadyExists(CarTypeAlreadyExistsException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(CarTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarTypeNotFound(CarTypeNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(UnknownCarTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnknownCarType(UnknownCarTypeException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(DealershipWithoutCompanyException.class)
    public ResponseEntity<ErrorResponse> handleDealershipWithoutCompany(DealershipWithoutCompanyException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrentPassword(InvalidCurrentPasswordException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(PasswordConfirmationMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordConfirmationMismatch(PasswordConfirmationMismatchException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(InvalidReservationPeriodException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReservationPeriod(InvalidReservationPeriodException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ErrorResponse> handleReservationConflict(ReservationConflictException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(InsuranceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInsuranceAlreadyExists(InsuranceAlreadyExistsException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }

    @ExceptionHandler(InsuranceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInsuranceNotFound(InsuranceNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return errorResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return errorResponse(HttpStatus.FORBIDDEN, "You do not have permission to perform this action", ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return errorResponse(HttpStatus.BAD_REQUEST, message, ex);
    }

    private ResponseEntity<ErrorResponse> errorResponse(HttpStatus status, String message, Exception ex) {
        log.warn("Handled {} ({}): {}", ex.getClass().getSimpleName(), status.value(), message);

        ErrorResponse body = new ErrorResponse()
                .status(status.value())
                .message(message)
                .timestamp(OffsetDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
