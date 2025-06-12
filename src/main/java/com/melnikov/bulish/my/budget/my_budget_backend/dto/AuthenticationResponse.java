package com.melnikov.bulish.my.budget.my_budget_backend.dto;

import com.melnikov.bulish.my.budget.my_budget_backend.enums.TokenType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "AuthenticationResponse Model Information")
public class AuthenticationResponse {

    @Schema(
        description = "Access Token",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEZW5pczM3ODM4MzQ0NjQ3IiwiaWF0IjoxNz",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accessToken;

    @Schema(
        description = "Refresh Token",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEZW5pczM3ODM4MzQ0NjQ3IiwiaWF0IjoxNz",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenType tokenType;
}