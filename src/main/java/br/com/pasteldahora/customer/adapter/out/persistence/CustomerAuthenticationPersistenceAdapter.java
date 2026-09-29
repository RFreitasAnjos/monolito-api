package br.com.pasteldahora.customer.adapter.out.persistence;

import br.com.pasteldahora.customer.application.port.out.CustomerAccessCodeData;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationRepositoryPort;
import br.com.pasteldahora.customer.application.port.out.CustomerSessionData;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerAuthenticationPersistenceAdapter
        implements CustomerAuthenticationRepositoryPort {

    private final SpringDataCustomerAccessCodeRepository codeRepository;
    private final SpringDataCustomerSessionRepository sessionRepository;

    CustomerAuthenticationPersistenceAdapter(
            SpringDataCustomerAccessCodeRepository codeRepository,
            SpringDataCustomerSessionRepository sessionRepository
    ) {
        this.codeRepository = codeRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void consumePendingCodes(UUID customerId, Instant consumedAt) {
        var pendingCodes = codeRepository.findByCustomerIdAndConsumedAtIsNull(customerId);
        pendingCodes.forEach(code -> codeRepository.save(new CustomerAccessCodeJpaEntity(
                code.getId(),
                code.getCustomerId(),
                code.getCodeHash(),
                code.getChannel(),
                code.getExpiresAt(),
                consumedAt,
                code.getFailedAttempts(),
                code.getCreatedAt(),
                code.getVersion()
        )));
    }

    @Override
    public CustomerAccessCodeData saveCode(CustomerAccessCodeData code) {
        return toData(codeRepository.save(toEntity(code)));
    }

    @Override
    public Optional<CustomerAccessCodeData> findLatestCode(UUID customerId) {
        return codeRepository
                .findFirstByCustomerIdAndConsumedAtIsNullOrderByCreatedAtDesc(customerId)
                .map(CustomerAuthenticationPersistenceAdapter::toData);
    }

    @Override
    public CustomerSessionData saveSession(CustomerSessionData session) {
        return toData(sessionRepository.save(toEntity(session)));
    }

    @Override
    public Optional<CustomerSessionData> findSessionByTokenHash(String tokenHash) {
        return sessionRepository.findByTokenHash(tokenHash)
                .map(CustomerAuthenticationPersistenceAdapter::toData);
    }

    private static CustomerAccessCodeJpaEntity toEntity(CustomerAccessCodeData code) {
        return new CustomerAccessCodeJpaEntity(
                code.id(), code.customerId(), code.codeHash(), code.channel(),
                code.expiresAt(), code.consumedAt(), code.failedAttempts(),
                code.createdAt(), code.version()
        );
    }

    private static CustomerAccessCodeData toData(CustomerAccessCodeJpaEntity code) {
        return new CustomerAccessCodeData(
                code.getId(), code.getCustomerId(), code.getCodeHash(), code.getChannel(),
                code.getExpiresAt(), code.getConsumedAt(), code.getFailedAttempts(),
                code.getCreatedAt(), code.getVersion()
        );
    }

    private static CustomerSessionJpaEntity toEntity(CustomerSessionData session) {
        return new CustomerSessionJpaEntity(
                session.id(), session.customerId(), session.tokenHash(), session.expiresAt(),
                session.revokedAt(), session.createdAt(), session.version()
        );
    }

    private static CustomerSessionData toData(CustomerSessionJpaEntity session) {
        return new CustomerSessionData(
                session.getId(), session.getCustomerId(), session.getTokenHash(),
                session.getExpiresAt(), session.getRevokedAt(), session.getCreatedAt(),
                session.getVersion()
        );
    }
}
