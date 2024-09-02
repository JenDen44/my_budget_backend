package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import com.melnikov.bulish.my.budget.my_budget_backend.purchase.PurchaseNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(PurchaseNotFoundException.class)
    public ResponseEntity<ApiErrorNotFound> purchaseExceptionHandler(PurchaseNotFoundException ex) {
        final ApiErrorNotFound apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorNotFound>(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }
}
