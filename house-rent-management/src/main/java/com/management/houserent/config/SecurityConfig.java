package com.management.houserent.config;

import com.management.houserent.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // public endpoints (note: use ant patterns or adjust as needed)
                        .requestMatchers(HttpMethod.GET, "/api/rooms/available", "/api/rooms/*").permitAll()
                        // role-based
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/rooms/**").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/api/owners/**").hasRole("OWNER")
                        .requestMatchers("/api/tenants/**").hasRole("TENANT")
                        .requestMatchers("/api/leases/**").hasAnyRole("ADMIN", "OWNER", "TENANT")
                        .requestMatchers("/api/payments/**").hasAnyRole("ADMIN", "OWNER", "TENANT")
                        .requestMatchers("/api/payments/webhook").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bills").hasAnyRole("OWNER", "ADMIN") // createBill
                        .requestMatchers(HttpMethod.GET, "/api/bills/lease/**").hasAnyRole("TENANT", "OWNER", "ADMIN") // getBillsForLease
                        .requestMatchers(HttpMethod.POST, "/api/bills/*/pay").hasRole("TENANT") // payBill
                        .anyRequest().authenticated()
                )
                // Do NOT call new DaoAuthenticationProvider() here — we build auth manager below
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Build AuthenticationManager using AuthenticationManagerBuilder from HttpSecurity.
     * This avoids directly instantiating DaoAuthenticationProvider and is the recommended pattern.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // configure userDetailsService + password encoder
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        // tighten allowed origins in prod
        cors.setAllowedOrigins(List.of("http://localhost:4200"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
