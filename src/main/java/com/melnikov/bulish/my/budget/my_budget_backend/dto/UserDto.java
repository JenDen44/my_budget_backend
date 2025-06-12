package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information")
public class UserDto extends AbstractDto {

    @Schema(description = "Username", example = "john_doe")
    @NotBlank
    @Size(min=3, max=50)
    private String username;

    @Schema(description = "password", example = "Password0202!")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
        message = "Need to have one special symbol (i.e., @, #, $, %, etc.)," +
            "Consists of at least one digit," +
            "Use at least one lowercase letter," +
            "Include a capital letter," +
            "Minimum length of 8 characters and the maximum length of 20 characters"
    )
    private String password;
}
