package br.com.pasteldahora.customer.application.port.out;

import java.time.Instant;
import java.util.UUID;

public record CustomerSessionData(
        UUID id,
        UUID customerId,
        String tokenHash,
        Instant expiresAt,
        Instant revokedAt,
        Instant createdAt,
        long version
) {

    public CustomerSessionData revoke(Instant now) {
        return new CustomerSessionData(
                id, customerId, tokenHash, expiresAt, now, createdAt, version
        );
    }
}
