package com.melnikov.bulish.my.budget.my_budget_backend.error_handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorNotFound> notFoundExceptionHandler(NoHandlerFoundException ex) {
        final var apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorNotFound> missingRequestParamsExceptionHandler(MissingServletRequestParameterException ex) {
        final var apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorValidation> fieldValidationExceptionHandler(ConstraintViolationException ex) {
        final List<ErrorField> errors = new ArrayList();

        for (final var violation : ex.getConstraintViolations()) {
            errors.add(new ErrorField(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        final var apiErrorValidation = new ApiErrorValidation(errors);

        return new ResponseEntity(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiErrorValidation> dataValidationExceptionHandler(DateTimeParseException ex) {
        final var apiErrorValidation = new ApiErrorValidation(ex.getLocalizedMessage());

        return new ResponseEntity(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorValidation> methodArgumentExceptionHandler(MethodArgumentNotValidException ex) {
        final List<ErrorField> errors = new ArrayList();

        errors.add(new ErrorField(ex.getFieldError().getField(), ex.getFieldError().getDefaultMessage()));

        final var apiErrorValidation = new ApiErrorValidation(errors);

        return new ResponseEntity(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

}