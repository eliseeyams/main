package org.example.controlbackendbf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@Profile({"dev","docker"}) // optional: nur in diesen Profilen so freigeben
public class SecurityConfig {

    private static final String[] SWAGGER = {
//            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v3/api-docs/**", // Standard
//            "/favicon.ico", "/error", "/", // optional, hilft gegen weitere 401
//            "/api/docs",       // dein umgestellter Pfad
//            "/api/docs/**"
    };

    @Bean
    @Profile("dev")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(SWAGGER).permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        );
        http.httpBasic(Customizer.withDefaults());
        http.formLogin(form -> form.disable());
        http.logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    @Profile("docker")
    SecurityFilterChain docker(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable());
//        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers(SWAGGER).permitAll()      // <- im Docker offen
//                .requestMatchers("/api/**").authenticated()
//                .anyRequest().denyAll()
//        );
//        http.httpBasic(Customizer.withDefaults());
//        http.formLogin(form -> form.disable());
//        http.logout(logout -> logout.disable());
//        return http.build();

//        http.csrf(csrf -> csrf.disable());
////        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers(SWAGGER).permitAll()
//                .requestMatchers("/api/**").authenticated()
//                .anyRequest().denyAll()
//        );
//        http.httpBasic(Customizer.withDefaults()); // 401 statt Redirect
//        http.formLogin(form -> form.disable());    // <-- das verhindert /login
//        http.logout(logout -> logout.disable());
//        return http.build();

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(SWAGGER).permitAll()   // <- MUSS vor anyRequest kommen
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll()
        );
        http.httpBasic(Customizer.withDefaults());  // 401 für geschützte API
        http.formLogin(form -> form.disable());     // kein Redirect auf /login
        http.logout(logout -> logout.disable());
        return http.build();
    }

//    @Bean
//    @Profile("dev")
//    SecurityFilterChain dev(HttpSecurity http) throws Exception {
//        // deine dev-Regeln …
//        return http.build();
//    }
}
