package com.melnikov.bulish.my.budget.my_budget_backend.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.*;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ApiError;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ApiErrorNotFound;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ApiErrorValidation;
import com.melnikov.bulish.my.budget.my_budget_backend.model.ErrorField;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ApiErrorNotFound error = new ApiErrorNotFound(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, WebRequest request) {
        ApiErrorValidation error = new ApiErrorValidation(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({SecurityException.class, AuthenticationException.class})
    public ResponseEntity<ApiError> handleSecurityExceptions(RuntimeException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NotificationException.class, WebSocketException.class})
    public ResponseEntity<ApiError> handleInternalExceptions(RuntimeException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.FORBIDDEN.value(), "Access denied");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleAccessDenied(BadCredentialsException ex, WebRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad credentials");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {

        List<ErrorField> errorFields = ex.getConstraintViolations().stream()
                .map(v -> new ErrorField(
                        v.getPropertyPath().toString(),
                        v.getMessage()))
                .collect(Collectors.toList());

        ApiErrorValidation error = new ApiErrorValidation(errorFields);
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<ErrorField> errorFields = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapToErrorField)
                .collect(Collectors.toList());

        ApiErrorValidation error = new ApiErrorValidation(errorFields);
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidEx = (InvalidFormatException) cause;

            if (invalidEx.getTargetType() != null && invalidEx.getTargetType().isEnum()) {
                Class<Enum> enumType = (Class<Enum>) invalidEx.getTargetType();
                String invalidValue = invalidEx.getValue().toString();

                String errorMessage = String.format(
                        "Invalid category value: '%s'. Accepted values: %s",
                        invalidValue,
                        Arrays.toString(enumType.getEnumConstants())
                );

                ApiError error = new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        errorMessage
                );

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("Malformed JSON request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorField mapToErrorField(FieldError fieldError) {
        return new ErrorField(fieldError.getField(), fieldError.getDefaultMessage());
    }
}