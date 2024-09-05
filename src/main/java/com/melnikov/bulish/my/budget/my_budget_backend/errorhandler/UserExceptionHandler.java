package com.melnikov.bulish.my.budget.my_budget_backend.errorhandler;

import com.melnikov.bulish.my.budget.my_budget_backend.user.UserNotFoundException;
import com.melnikov.bulish.my.budget.my_budget_backend.user.UserValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorNotFound> userExceptionHandler(UserNotFoundException ex) {
        final ApiErrorNotFound apiErrorNotFound = new ApiErrorNotFound(ex.getLocalizedMessage());

        return new ResponseEntity<ApiErrorNotFound>(apiErrorNotFound, new HttpHeaders(), apiErrorNotFound.getCode());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ApiErrorValidation> userExceptionHandler(UserValidationException ex) {
        final List<ErrorField> errors = new ArrayList();
        if (ex.getLocalizedMessage().contains("password")) errors.add(new ErrorField("password", ex.getLocalizedMessage()));
            else errors.add(new ErrorField("username", ex.getLocalizedMessage()));

        final ApiErrorValidation apiErrorValidation = new ApiErrorValidation(errors);

        return new ResponseEntity<ApiErrorValidation>(apiErrorValidation, new HttpHeaders(), apiErrorValidation.getCode());
    }
}
