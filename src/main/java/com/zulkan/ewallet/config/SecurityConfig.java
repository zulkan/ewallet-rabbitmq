package com.zulkan.ewallet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zulkan.ewallet.filter.AuthFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@EnableTransactionManagement
public class SecurityConfig {

    private final AuthFilter authFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public SecurityConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
//                .cors((corsConfigurer) -> corsConfigurationSource())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(new AntPathRequestMatcher("/users/create_user/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(authFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exc -> exc.authenticationEntryPoint(
                        (request, response, authException) -> {
//                            authException.printStackTrace();
                            Map<String, String> message = new HashMap<>();
                            message.put("message", authException.getMessage());
                            String messageStr = objectMapper.writeValueAsString(message);
                            PrintWriter out = response.getWriter();
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            out.print(messageStr);
                            out.flush();
                        }
                ))
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public OpenAPI publicApi() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }

}
