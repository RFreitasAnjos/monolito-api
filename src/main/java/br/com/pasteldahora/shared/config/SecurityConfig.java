package br.com.pasteldahora.shared.config;

import br.com.pasteldahora.customer.adapter.in.api.CustomerBearerAuthenticationFilter;
import br.com.pasteldahora.customer.application.port.in.CustomerAuthenticationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

/**
 * Mantém as superfícies employee e customer em namespaces e fluxos distintos.
 */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Profile("h2")
    @Order(1)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain catalogApiSecurityFilterChain(
            HttpSecurity http,
            @Value("${app.catalog.cors.allowed-origins:http://localhost:4200,http://localhost:5173}")
            String allowedOrigins
    ) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList());
        corsConfiguration.setAllowedMethods(java.util.List.of("GET", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(java.util.List.of("Content-Type"));
        corsConfiguration.setAllowCredentials(false);

        http
                .securityMatcher("/api/catalog/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/catalog/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/catalog/**").permitAll()
                        .anyRequest().denyAll()
                );
        return http.build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain customerApiSecurityFilterChain(
            HttpSecurity http,
            CustomerAuthenticationUseCase customerAuthenticationUseCase,
            @Value("${app.customer.cors.allowed-origins:http://localhost:4200,http://localhost:5173}")
            String allowedOrigins
    ) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList());
        corsConfiguration.setAllowedMethods(
                java.util.List.of("GET", "POST", "PUT", "PATCH", "OPTIONS")
        );
        corsConfiguration.setAllowedHeaders(java.util.List.of("Content-Type", "Authorization"));
        corsConfiguration.setAllowCredentials(true);

        http
                .securityMatcher("/api/customers/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(
                        new CustomerBearerAuthenticationFilter(customerAuthenticationUseCase),
                        AnonymousAuthenticationFilter.class
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/customers/auth/code",
                                "/api/customers/auth/verify"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/customers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/customers/me")
                        .hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/customers/auth/logout")
                        .hasRole("CUSTOMER")
                        .anyRequest().hasAnyRole("ADMIN", "MANAGER")
                );
        return http.build();
    }

    @Bean
    @Order(4)
    SecurityFilterChain employeeMvcSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/employee/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/employee/login").permitAll()
                        .anyRequest().hasAnyRole("ADMIN", "MANAGER", "OPERATOR")
                )
                .formLogin(form -> form
                        .loginPage("/employee/login")
                        .loginProcessingUrl("/employee/login")
                        .defaultSuccessUrl("/employee/dashboard", true)
                        .failureUrl("/employee/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/employee/logout")
                        .logoutSuccessUrl("/employee/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    @Order(5)
    SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**"
                ).permitAll()
                .anyRequest().denyAll()
        );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
