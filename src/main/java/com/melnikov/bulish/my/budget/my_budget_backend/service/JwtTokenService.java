package com.melnikov.bulish.my.budget.my_budget_backend.service;

import com.melnikov.bulish.my.budget.my_budget_backend.model.GeneratedToken;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtTokenService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    GeneratedToken generateAccessToken(UserDetails userDetails);

    GeneratedToken generateRefreshToken(UserDetails userDetails);

    String resolveToken(String authorizationHeader);

    boolean isTokenValid(String token);

    boolean validateAccessToken(String token, UserDetails userDetails);

    boolean validateRefreshToken(String token, UserDetails userDetails);
}
