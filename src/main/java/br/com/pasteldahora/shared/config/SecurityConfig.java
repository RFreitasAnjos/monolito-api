package br.com.pasteldahora.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
    @Order(3)
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
    @Order(4)
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
