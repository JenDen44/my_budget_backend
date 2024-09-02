package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorValidation> purchaseExceptionHandler(ConstraintViolationException ex) {
        final List<ErrorField> errors = new ArrayList();

     /*   for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new ErrorField(violation.getPropertyPath().toString(), violation.getMessage()));
        }*/

        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(errors);

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }
}