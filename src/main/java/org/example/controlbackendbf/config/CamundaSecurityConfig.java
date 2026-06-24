package org.example.controlbackendbf.config;

import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Integriert deine bestehende JWT-Sicherheit mit Camunda.
 *
 * STRATEGIE:
 * - /camunda/** → Camunda eigene Auth (Basic Auth für Cockpit/Tasklist)
 * - /api/**     → Deine JWT-Auth (wie bisher)
 * - /process/** → JWT-Auth (neue Camunda-Endpoints die du baust)
 */
@Configuration
public class CamundaSecurityConfig {

    /**
     * Camunda Cockpit & REST API: eigene Authentifizierung.
     * Camunda bringt seinen eigenen User-Store mit (siehe application.yml: admin/admin).
     * Diese Chain hat höhere Priorität als deine JWT-Chain.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain camundaSecurityChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/camunda/**", "/engine-rest/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});  // Camunda Cockpit nutzt Basic Auth

        return http.build();
    }

    /**
     * Deine API-Endpunkte: JWT wie bisher.
     * Füge hier dein bestehendes JwtAuthFilter ein.
     *
     * Ersetze "JwtAuthFilter" mit dem Namen deiner eigenen Klasse.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityChain(HttpSecurity http,
            /* JwtAuthFilter jwtAuthFilter */ Object placeholder) throws Exception {
        http
                .securityMatcher("/api/**", "/process/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Login/Register ohne Auth
                        .anyRequest().authenticated()
                );
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // ↑ Diese Zeile mit deinem JwtAuthFilter einkommentieren

        return http.build();
    }
}
