package com.dashboard.gateway.filter;

import com.dashboard.gateway.dto.ValidateTokenResponse;
import com.dashboard.gateway.service.AuthValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    
    private final AuthValidationService authValidationService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip authentication for auth endpoints
        if (path.startsWith("/api/auth/register") || 
            path.startsWith("/api/auth/login") ||
            path.startsWith("/api/auth/password-recovery") ||
            path.startsWith("/api/auth/reset-password")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // For all other endpoints, validate token
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Missing or invalid Authorization header\"}");
            return;
        }
        
        String token = authHeader.substring(7);
        
        // Validate token synchronously
        ValidateTokenResponse validationResult = validateTokenSync(token);
        
        if (!validationResult.isValid()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"" + validationResult.getMessage() + "\"}");
            return;
        }
        
        // Add user email to request attribute for downstream services
        request.setAttribute("userEmail", validationResult.getEmail());
        
        filterChain.doFilter(request, response);
    }
    
    private ValidateTokenResponse validateTokenSync(String token) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            final ValidateTokenResponse[] result = new ValidateTokenResponse[1];
            
            authValidationService.validateToken(token)
                .subscribe(
                    response -> {
                        result[0] = response;
                        latch.countDown();
                    },
                    error -> {
                        log.error("Error validating token", error);
                        result[0] = new ValidateTokenResponse(false, null, "Error validating token");
                        latch.countDown();
                    }
                );
            
            latch.await(5, TimeUnit.SECONDS);
            return result[0] != null ? result[0] : new ValidateTokenResponse(false, null, "Timeout validating token");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while validating token", e);
            return new ValidateTokenResponse(false, null, "Error validating token");
        }
    }
}
