package com.melnikov.bulish.my.budget.my_budget_backend.controller;

import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.AuthenticationResponse;
import com.melnikov.bulish.my.budget.my_budget_backend.dto.RefreshTokenRequest;
import com.melnikov.bulish.my.budget.my_budget_backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authorization/Authentication")
@Validated
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(
        summary = "Register new user",
        description = "Create a new user account",
        responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "User created successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = AuthenticationResponse.class),
                                examples = @ExampleObject(
                                        value = "{\"accessToken\":\"eyJhbGci...\", \"refreshToken\":\"eyJhbGci...\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Validation error",
                        content = @Content(
                                examples = @ExampleObject(
                                        name = "ValidationErrorExample",
                                        value = """
                                                {
                                                  "code": 422,
                                                  "message": "Validation Error",
                                                  "fields": [
                                                    {
                                                      "field": "password",
                                                      "message": "Password must have: 1 uppercase, 1 lowercase, 1 digit, 1 special character, 8-30 length"
                                                    }
                                                  ]
                                                }
                                                """
                                )
                        )
                )
        }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public AuthenticationResponse registerUser(@RequestBody @Valid AuthenticationRequest request) {
        return service.register(request);
    }

    @Operation(
        summary = "Authenticate user",
        description = "Login with username and password",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Authentication successful",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = AuthenticationResponse.class),
                                examples = @ExampleObject(
                                        value = "{\"accessToken\":\"eyJhbGci...\", \"refreshToken\":\"eyJhbGci...\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"code\": 404, \"message\":\"user not found with id 1\", \"fields\": \"null\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Validation error",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        name = "ValidationErrorExample",
                                        value = """
                                                {
                                                  "code": 422,
                                                  "message": "Validation Error",
                                                  "fields": [
                                                    {
                                                      "field": "username",
                                                      "message": "username is required"
                                                    }
                                                  ]
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad credentials",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"code\": 400, \"message\":\"Wrong login or password\"}"
                                )
                        )
                )
        }
    )
    @PostMapping("/login")
    public AuthenticationResponse loginUser(@RequestBody @Valid AuthenticationRequest request) {
        return service.login(request);
    }

    @Operation(
        summary = "Refresh access token",
        description = "Obtain new access token using refresh token",
        security = @SecurityRequirement(name = "refreshToken"),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Token refreshed successfully",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = AuthenticationResponse.class),
                                examples = @ExampleObject(
                                        value = "{\"accessToken\":\"eyJhbGci...\", \"refreshToken\":\"eyJhbGci...\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"code\": 404, \"message\":\"user not found\", \"fields\": \"null\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Invalid or expired refresh token",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"code\": 401, \"message\":\"Invalid or expired token\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Validation error",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(
                                        value = "{\"code\": 422, \"message\":\"Validation Error: Extracted username from token is null\", \"fields\": \"null\"}"
                                )
                        )
                )
        }
    )
    @PostMapping("/refresh")
    public AuthenticationResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return service.refreshToken(request.getRefreshToken());
    }
}
