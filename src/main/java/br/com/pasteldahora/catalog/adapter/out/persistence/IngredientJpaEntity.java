package br.com.pasteldahora.catalog.adapter.out.persistence;

import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;
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
@Table(name = "ingredients")
class IngredientJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String sku;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_unit", nullable = false, length = 20)
    private UnitOfMeasure stockUnit;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 160)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 160)
    private String updatedBy;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Column(name = "deactivated_by", length = 160)
    private String deactivatedBy;

    @Version
    private long version;

    protected IngredientJpaEntity() {
    }

    IngredientJpaEntity(
            UUID id,
            String sku,
            String name,
            String description,
            UnitOfMeasure stockUnit,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.stockUnit = stockUnit;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        this.version = version;
    }

    UUID getId() { return id; }
    String getSku() { return sku; }
    String getName() { return name; }
    String getDescription() { return description; }
    UnitOfMeasure getStockUnit() { return stockUnit; }
    boolean isActive() { return active; }
    Instant getCreatedAt() { return createdAt; }
    Instant getUpdatedAt() { return updatedAt; }
    String getCreatedBy() { return createdBy; }
    String getUpdatedBy() { return updatedBy; }
    Instant getDeactivatedAt() { return deactivatedAt; }
    String getDeactivatedBy() { return deactivatedBy; }
    long getVersion() { return version; }
}
