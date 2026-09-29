package br.com.pasteldahora.customer.application.port.in;

import br.com.pasteldahora.customer.domain.model.Customer;

import java.time.Instant;

public record CustomerAuthentication(
        Customer customer,
        String accessToken,
        Instant expiresAt
) {
}
