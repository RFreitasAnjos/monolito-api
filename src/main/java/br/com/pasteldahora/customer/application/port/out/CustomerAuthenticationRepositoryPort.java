package br.com.pasteldahora.customer.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CustomerAuthenticationRepositoryPort {

    void consumePendingCodes(UUID customerId, Instant consumedAt);

    CustomerAccessCodeData saveCode(CustomerAccessCodeData code);

    Optional<CustomerAccessCodeData> findLatestCode(UUID customerId);

    CustomerSessionData saveSession(CustomerSessionData session);

    Optional<CustomerSessionData> findSessionByTokenHash(String tokenHash);
}
