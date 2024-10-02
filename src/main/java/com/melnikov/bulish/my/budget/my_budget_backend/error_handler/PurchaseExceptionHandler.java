package com.melnikov.bulish.my.budget.my_budget_backend.error_handler;

import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PurchaseExceptionHandler {

    @ExceptionHandler(PurchaseNotFoundException.class)
    public ResponseEntity<ApiErrorNotFound> purchaseExceptionHandler(PurchaseNotFoundException ex) {
        final var apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }
}