package dev.hiwa.iticket.controller;


import dev.hiwa.iticket.domain.dto.response.ApiErrorResponse;
import dev.hiwa.iticket.domain.dto.response.ApiValidationErrorResponse;
import dev.hiwa.iticket.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());

        return ResponseEntity.internalServerError().body(apiErrorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex
                .getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));


        ApiValidationErrorResponse apiError = new ApiValidationErrorResponse();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setMessage("Invalid input data");
        apiError.setErrors(errors);

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex
    ) {
        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    @ExceptionHandler(ResourceConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceConstraintViolationException(
            ResourceAlreadyExistsException ex
    ) {
        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
            AuthenticationException ex
    ) {
        ApiErrorResponse apiErrorResponse =
                new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Incorrect username or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorResponse);
    }

    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<ApiErrorResponse> handleQrCodeGenerationException(
            AuthenticationException ex
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to generate QR code "
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    @ExceptionHandler(TicketSoldOutException.class)
    public ResponseEntity<ApiErrorResponse> handleTicketSoldOutException(
            AuthenticationException ex
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Tickets are sold out for this ticket type"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }
}
