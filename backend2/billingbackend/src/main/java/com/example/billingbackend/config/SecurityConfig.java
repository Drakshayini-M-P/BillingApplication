package com.example.billingbackend.config;

import com.example.billingbackend.security.JwtAuthenticationFilter;
import com.example.billingbackend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        // --- PUBLIC ENDPOINTS ---
                        // Allow anyone to access the login and registration endpoints.
                        .requestMatchers("/auth/**").permitAll()
                        
                        // --- ADMIN-ONLY ENDPOINTS ---
                        // Only users with the ADMIN role can access any endpoint under /admin.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // --- ROLE-BASED WRITE PERMISSIONS (POST, PUT, DELETE) ---
                        // Only ADMIN and ACCOUNT users can manage customers.
                        .requestMatchers("/customers/**").hasAnyRole("ADMIN", "ACCOUNT")
                        // Only ADMIN and ACCOUNT users can create, update, or delete products.
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAnyRole("ADMIN", "ACCOUNT")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "ACCOUNT")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "ACCOUNT")
                        // Only ADMIN and ACCOUNT users can create, update, or delete invoices.
                        .requestMatchers(HttpMethod.POST, "/invoices").hasAnyRole("ADMIN", "ACCOUNT")
                        .requestMatchers(HttpMethod.PUT, "/invoices/**").hasAnyRole("ADMIN", "ACCOUNT")
                        .requestMatchers(HttpMethod.DELETE, "/invoices/**").hasAnyRole("ADMIN", "ACCOUNT")

                        // --- GENERAL AUTHENTICATED PERMISSIONS ---
                        // Allow ANY authenticated user (including CUSTOMER) to perform any action on the payments endpoint.
                        // The controller logic handles the data filtering for security.
                        .requestMatchers("/payments/**").authenticated()
                        // Allow ANY authenticated user to VIEW data (GET requests).
                        .requestMatchers(HttpMethod.GET, "/products/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/invoices/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/dashboard/**").authenticated()
                        
                        // --- FALLBACK RULE ---
                        // All other requests that haven't been matched yet must be authenticated.
                        .anyRequest().authenticated()
                );

        // Add our custom JWT filter to the security chain before the default login filter.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}