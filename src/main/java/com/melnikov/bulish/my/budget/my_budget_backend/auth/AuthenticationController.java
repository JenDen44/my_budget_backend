package com.melnikov.bulish.my.budget.my_budget_backend.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authorization/Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(
            description = "Endpoint for reservation new user",
            summary = "If you need to register new user, please use this endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),

                    @ApiResponse(
                            description = "Validation error",
                            responseCode = "422"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody AuthenticationRequest request) {

        return ResponseEntity.ok(service.register(request));
    }

    @Operation(
            description = "Endpoint for login",
            summary = "If you need to login in the system, please use this endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),

                    @ApiResponse(
                            description = "Validation error",
                            responseCode = "422"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@Valid @RequestBody AuthenticationRequest request) {

        return ResponseEntity.ok(service.login(request));
    }

    @Operation(
            description = "Endpoint for refresh token",
            summary = "If you need to refresh expired token, please use this endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),

                    @ApiResponse(
                            description = "Unauthorized/Invalid token",
                            responseCode = "401"
                    )
            }
    )
    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {

        return ResponseEntity.ok(service.refreshToken(request, response));
    }
}
