package com.melnikov.bulish.my.budget.my_budget_backend.filter;

import com.melnikov.bulish.my.budget.my_budget_backend.constants.JWTConstants;
import com.melnikov.bulish.my.budget.my_budget_backend.exceptions.ValidationException;
import com.melnikov.bulish.my.budget.my_budget_backend.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var jwt = parseJwt(request);
            var username = jwtTokenService.extractUsername(jwt);

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired:", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        } catch (JwtException | UsernameNotFoundException e) {
            logger.error("JWT error", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Set.of("/register", "/login", "/refresh", "/swagger-resources","/v3/api-docs", "/swagger-ui/",
                "/swagger-ui.html", "/swagger-ui/index.html", "/api-docs", "/ws").contains(request.getServletPath());
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(JWTConstants.AUTH_HEADER);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(JWTConstants.BEARER_PREFIX)) {
            return headerAuth.substring(JWTConstants.BEARER_PREFIX.length());
        }
        throw new ValidationException("JWT", "Incorrect token representation");
    }
}