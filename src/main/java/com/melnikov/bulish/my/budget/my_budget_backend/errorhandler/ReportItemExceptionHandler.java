package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import com.melnikov.bulish.my.budget.my_budget_backend.report.ReportItemNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ReportItemExceptionHandler {

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiErrorValidation> reportExceptionHandler(DateTimeParseException ex) {
        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }

    @ExceptionHandler(ReportItemNotFoundException.class)
    public ResponseEntity<ApiErrorValidation> reportExceptionHandler(ReportItemNotFoundException ex) {
        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }
}