package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Schema(description = "RefreshTokenRequest Model Information")
public class RefreshTokenRequest {

    @Schema(
            description = "refreshToken",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEZW5pczM3ODM4MzQ0NjQ3IiwiaWF0IjoxNz",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Refresh token must not be blank")
    @Pattern(
            regexp = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$",  //This regex checks for the basic 3-part JWT structure:
            message = "Invalid token format. Must be a valid JWT token"
    )
    private String refreshToken;
}
