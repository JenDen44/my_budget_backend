package com.melnikov.bulish.my.budget.my_budget_backend.auth;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "AuthenticationRequest Model Information")
public class AuthenticationRequest {

    @Schema(description = "Username should contain name or email", example = "Denis")
    @NotBlank(message = "username should be filled")
    @Size(min = 7, message = "username has to have minimum 7 symbols")
    private String username;

    @Schema(description = "Password should be strong, need to have one special symbol," +
            "one lowercase and capital letter, one digit", example = "Password0404$")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Need to have one special symbol (i.e., @, #, $, %, etc.)," +
                      "Consists of at least one digit," +
                      "Use at least one lowercase letter," +
                      "Include a capital letter," +
                      "Minimum length of 8 characters and the maximum length of 20 characters")
    private String password;

}