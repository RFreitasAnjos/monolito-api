package br.com.pasteldahora.customer.adapter.in.api;

import br.com.pasteldahora.customer.application.port.in.CustomerAuthentication;

import java.time.Instant;

public record CustomerAuthenticationResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        CustomerResponse customer
) {

    static CustomerAuthenticationResponse from(CustomerAuthentication authentication) {
        return new CustomerAuthenticationResponse(
                authentication.accessToken(),
                "Bearer",
                authentication.expiresAt(),
                CustomerResponse.from(authentication.customer())
        );
    }
}
