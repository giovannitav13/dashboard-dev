package com.dashboard.gateway.config;

import com.dashboard.gateway.filter.AuthenticationFilter;
import com.dashboard.gateway.service.AuthValidationService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilter(AuthValidationService authValidationService) {
        AuthenticationFilter filter = new AuthenticationFilter(authValidationService);
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
