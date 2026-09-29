package br.com.pasteldahora.order.adapter.out.persistence;

import br.com.pasteldahora.order.domain.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_orders")
class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 160)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 160)
    private String updatedBy;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completed_by", length = 160)
    private String completedBy;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by", length = 160)
    private String cancelledBy;

    @Version
    private long version;

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(
            UUID id,
            UUID customerId,
            OrderStatus status,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant completedAt,
            String completedBy,
            Instant cancelledAt,
            String cancelledBy,
            long version
    ) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.completedAt = completedAt;
        this.completedBy = completedBy;
        this.cancelledAt = cancelledAt;
        this.cancelledBy = cancelledBy;
        this.version = version;
    }

    void replaceItems(List<OrderItemJpaEntity> updatedItems) {
        items.clear();
        items.addAll(updatedItems);
    }

    UUID getId() {
        return id;
    }

    UUID getCustomerId() {
        return customerId;
    }

    OrderStatus getStatus() {
        return status;
    }

    List<OrderItemJpaEntity> getItems() {
        return items;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }

    String getCreatedBy() {
        return createdBy;
    }

    String getUpdatedBy() {
        return updatedBy;
    }

    Instant getCompletedAt() {
        return completedAt;
    }

    String getCompletedBy() {
        return completedBy;
    }

    Instant getCancelledAt() {
        return cancelledAt;
    }

    String getCancelledBy() {
        return cancelledBy;
    }

    long getVersion() {
        return version;
    }
}
