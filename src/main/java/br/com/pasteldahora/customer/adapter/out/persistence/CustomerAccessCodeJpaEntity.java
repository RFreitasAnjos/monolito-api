package br.com.pasteldahora.customer.adapter.out.persistence;

import br.com.pasteldahora.customer.domain.model.CustomerAccessChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_access_codes")
class CustomerAccessCodeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "code_hash", nullable = false, length = 100)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerAccessChannel channel;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private long version;

    protected CustomerAccessCodeJpaEntity() {
    }

    CustomerAccessCodeJpaEntity(
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
        this.id = id;
        this.customerId = customerId;
        this.codeHash = codeHash;
        this.channel = channel;
        this.expiresAt = expiresAt;
        this.consumedAt = consumedAt;
        this.failedAttempts = failedAttempts;
        this.createdAt = createdAt;
        this.version = version;
    }

    UUID getId() { return id; }
    UUID getCustomerId() { return customerId; }
    String getCodeHash() { return codeHash; }
    CustomerAccessChannel getChannel() { return channel; }
    Instant getExpiresAt() { return expiresAt; }
    Instant getConsumedAt() { return consumedAt; }
    int getFailedAttempts() { return failedAttempts; }
    Instant getCreatedAt() { return createdAt; }
    long getVersion() { return version; }
}
