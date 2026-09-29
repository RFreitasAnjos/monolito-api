package br.com.pasteldahora.customer.adapter.in.api;

import br.com.pasteldahora.customer.application.port.in.CustomerAuthenticationUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class CustomerBearerAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final CustomerAuthenticationUseCase authenticationUseCase;

    public CustomerBearerAuthenticationFilter(
            CustomerAuthenticationUseCase authenticationUseCase
    ) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null
                && authorization.startsWith(BEARER_PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String token = authorization.substring(BEARER_PREFIX.length()).trim();
                var customer = authenticationUseCase.authenticate(token);
                var authentication = new UsernamePasswordAuthenticationToken(
                        customer,
                        token,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
