package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import com.melnikov.bulish.my.budget.my_budget_backend.auth.TokenValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
        final ApiErrorNotFound apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorNotFound>(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorNotFound> missingRequestParamsExceptionHandler(MissingServletRequestParameterException ex) {
        final ApiErrorNotFound apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorNotFound>(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorValidation> fieldValidationExceptionHandler(ConstraintViolationException ex) {
        final List<ErrorField> errors = new ArrayList();

        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new ErrorField(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(errors);

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiErrorValidation> dataValidationExceptionHandler(DateTimeParseException ex) {
        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiErrorValidation> tokenExceptionHandler(TokenValidationException ex) {
        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

}