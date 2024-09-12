package com.melnikov.bulish.my.budget.my_budget_backend.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "AuthenticationResponse Model Information")
public class AuthenticationResponse {

    @Schema(description = "Return access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEZW5pczM3ODM4MzQ0NjQ3IiwiaWF0IjoxNz")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Return refresh Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEZW5pczM3ODM4MzQ0NjQ3IiwiaWF0IjoxNz")
    @JsonProperty("refresh_token")
    private String refreshToken;
}