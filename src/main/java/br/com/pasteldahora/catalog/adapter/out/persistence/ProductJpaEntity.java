package br.com.pasteldahora.catalog.adapter.out.persistence;

import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
class ProductJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String sku;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "sale_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal salePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_policy", nullable = false, length = 30)
    private InventoryPolicy inventoryPolicy;

    @Column(nullable = false)
    private boolean customizable;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "product_ingredients",
            joinColumns = @JoinColumn(name = "product_id")
    )
    private List<ProductIngredientEmbeddable> ingredients = new ArrayList<>();

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

    protected ProductJpaEntity() {
    }

    ProductJpaEntity(
            UUID id,
            String sku,
            String name,
            String description,
            UUID categoryId,
            BigDecimal salePrice,
            InventoryPolicy inventoryPolicy,
            boolean customizable,
            List<ProductIngredientEmbeddable> ingredients,
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
        this.categoryId = categoryId;
        this.salePrice = salePrice;
        this.inventoryPolicy = inventoryPolicy;
        this.customizable = customizable;
        this.ingredients = new ArrayList<>(ingredients);
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    String getSku() {
        return sku;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    UUID getCategoryId() {
        return categoryId;
    }

    BigDecimal getSalePrice() {
        return salePrice;
    }

    InventoryPolicy getInventoryPolicy() {
        return inventoryPolicy;
    }

    boolean isCustomizable() {
        return customizable;
    }

    List<ProductIngredientEmbeddable> getIngredients() {
        return List.copyOf(ingredients);
    }

    boolean isActive() {
        return active;
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

    Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    String getDeactivatedBy() {
        return deactivatedBy;
    }

    long getVersion() {
        return version;
    }
}
