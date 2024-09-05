package com.melnikov.bulish.my.budget.my_budget_backend.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "username should be filled")
    @Size(min = 7, message = "username has to have minimum 7 symbols")
    private String username;

    @NotBlank(message = "password should be filled")
    @Size(min = 7, message = "password has to have minimum 8 symbols")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$")
    private String password;

}