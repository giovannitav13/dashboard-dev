package com.dashboard.gateway.service;

import com.dashboard.gateway.dto.ValidateTokenRequest;
import com.dashboard.gateway.dto.ValidateTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthValidationService {
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    private final WebClient.Builder webClientBuilder;
    
    public Mono<ValidateTokenResponse> validateToken(String token) {
        WebClient webClient = webClientBuilder.baseUrl(authServiceUrl).build();
        
        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken(token);
        
        return webClient.post()
                .uri("/api/auth/validate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ValidateTokenResponse.class)
                .doOnError(error -> log.error("Error validating token with auth-service", error))
                .onErrorReturn(new ValidateTokenResponse(false, null, "Error validating token"));
    }
}
