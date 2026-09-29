package br.com.pasteldahora.customer.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_sessions")
class CustomerSessionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private long version;

    protected CustomerSessionJpaEntity() {
    }

    CustomerSessionJpaEntity(
            UUID id,
            UUID customerId,
            String tokenHash,
            Instant expiresAt,
            Instant revokedAt,
            Instant createdAt,
            long version
    ) {
        this.id = id;
        this.customerId = customerId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
        this.version = version;
    }

    UUID getId() { return id; }
    UUID getCustomerId() { return customerId; }
    String getTokenHash() { return tokenHash; }
    Instant getExpiresAt() { return expiresAt; }
    Instant getRevokedAt() { return revokedAt; }
    Instant getCreatedAt() { return createdAt; }
    long getVersion() { return version; }
}
