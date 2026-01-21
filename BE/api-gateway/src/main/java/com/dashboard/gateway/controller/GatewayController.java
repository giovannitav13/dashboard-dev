package com.dashboard.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GatewayController {
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    @Value("${projects.service.url}")
    private String projectsServiceUrl;
    
    private final RestTemplate restTemplate;
    
    @RequestMapping(value = "/auth/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> routeToAuthService(HttpServletRequest request) {
        String path = request.getRequestURI().replace("/api/auth", "");
        String targetUrl = authServiceUrl + "/api/auth" + path;
        return forwardRequest(request, targetUrl);
    }
    
    @RequestMapping(value = "/projects/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> routeToProjectsService(HttpServletRequest request) {
        String path = request.getRequestURI().replace("/api/projects", "");
        String targetUrl = projectsServiceUrl + "/api/projects" + path;
        return forwardRequest(request, targetUrl);
    }
    
    private ResponseEntity<?> forwardRequest(HttpServletRequest request, String targetUrl) {
        try {
            HttpMethod method = HttpMethod.valueOf(request.getMethod());
            HttpHeaders headers = new HttpHeaders();
            
            // Copy all headers from original request
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (!headerName.equalsIgnoreCase("host") && !headerName.equalsIgnoreCase("content-length")) {
                    headers.add(headerName, request.getHeader(headerName));
                }
            }
            
            // Add user email if available
            String userEmail = (String) request.getAttribute("userEmail");
            if (userEmail != null) {
                headers.add("X-User-Email", userEmail);
            }
            
            Object body = null;
            if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                try {
                    byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
                    if (bodyBytes.length > 0) {
                        body = new String(bodyBytes, StandardCharsets.UTF_8);
                    }
                } catch (IOException e) {
                    log.warn("Could not read request body", e);
                }
            }
            
            HttpEntity<?> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl + (request.getQueryString() != null ? "?" + request.getQueryString() : ""),
                method,
                entity,
                String.class
            );
            
            return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
                
        } catch (Exception e) {
            log.error("Error forwarding request to {}", targetUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Error forwarding request: " + e.getMessage() + "\"}");
        }
    }
}
