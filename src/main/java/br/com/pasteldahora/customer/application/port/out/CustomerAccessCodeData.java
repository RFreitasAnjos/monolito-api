package br.com.pasteldahora.customer.application.port.out;

import br.com.pasteldahora.customer.domain.model.CustomerAccessChannel;

import java.time.Instant;
import java.util.UUID;

public record CustomerAccessCodeData(
        UUID id,
        UUID customerId,
        String codeHash,
        CustomerAccessChannel channel,
        Instant expiresAt,
        Instant consumedAt,
        int failedAttempts,
        Instant createdAt,
        long version
) {

    public CustomerAccessCodeData failAttempt() {
        return new CustomerAccessCodeData(
                id, customerId, codeHash, channel, expiresAt, consumedAt,
                failedAttempts + 1, createdAt, version
        );
    }

    public CustomerAccessCodeData consume(Instant now) {
        return new CustomerAccessCodeData(
                id, customerId, codeHash, channel, expiresAt, now,
                failedAttempts, createdAt, version
        );
    }
}
