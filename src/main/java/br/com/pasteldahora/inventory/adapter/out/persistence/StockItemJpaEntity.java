package br.com.pasteldahora.inventory.adapter.out.persistence;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_items")
class StockItemJpaEntity {

    @Id
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private CatalogItemType itemType;

    @Column(name = "current_quantity", nullable = false, precision = 19, scale = 3)
    private BigDecimal currentQuantity;

    @Column(name = "minimum_quantity", nullable = false, precision = 19, scale = 3)
    private BigDecimal minimumQuantity;

    @Column(name = "average_unit_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal averageUnitCost;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 160)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 160)
    private String updatedBy;

    @Version
    private long version;

    protected StockItemJpaEntity() {
    }

    StockItemJpaEntity(
            UUID id,
            UUID itemId,
            CatalogItemType itemType,
            BigDecimal currentQuantity,
            BigDecimal minimumQuantity,
            BigDecimal averageUnitCost,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            long version
    ) {
        this.id = id;
        this.itemId = itemId;
        this.itemType = itemType;
        this.currentQuantity = currentQuantity;
        this.minimumQuantity = minimumQuantity;
        this.averageUnitCost = averageUnitCost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    UUID getItemId() {
        return itemId;
    }

    CatalogItemType getItemType() {
        return itemType;
    }

    BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    BigDecimal getMinimumQuantity() {
        return minimumQuantity;
    }

    BigDecimal getAverageUnitCost() {
        return averageUnitCost;
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

    long getVersion() {
        return version;
    }
}
