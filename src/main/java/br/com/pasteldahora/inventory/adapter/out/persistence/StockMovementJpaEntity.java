package br.com.pasteldahora.inventory.adapter.out.persistence;

import br.com.pasteldahora.inventory.domain.model.StockMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
class StockMovementJpaEntity {

    @Id
    private UUID id;

    @Column(name = "stock_item_id", nullable = false)
    private UUID stockItemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockMovementType type;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantity;

    @Column(name = "resulting_balance", nullable = false, precision = 19, scale = 3)
    private BigDecimal resultingBalance;

    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitCost;

    @Column(nullable = false, length = 250)
    private String reason;

    @Column(nullable = false, length = 160)
    private String actor;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected StockMovementJpaEntity() {
    }

    StockMovementJpaEntity(
            UUID id,
            UUID stockItemId,
            StockMovementType type,
            BigDecimal quantity,
            BigDecimal resultingBalance,
            BigDecimal unitPrice,
            BigDecimal unitCost,
            String reason,
            String actor,
            Instant occurredAt
    ) {
        this.id = id;
        this.stockItemId = stockItemId;
        this.type = type;
        this.quantity = quantity;
        this.resultingBalance = resultingBalance;
        this.unitPrice = unitPrice;
        this.unitCost = unitCost;
        this.reason = reason;
        this.actor = actor;
        this.occurredAt = occurredAt;
    }

    UUID getId() {
        return id;
    }

    UUID getStockItemId() {
        return stockItemId;
    }

    StockMovementType getType() {
        return type;
    }

    BigDecimal getQuantity() {
        return quantity;
    }

    BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    BigDecimal getUnitPrice() {
        return unitPrice;
    }

    BigDecimal getUnitCost() {
        return unitCost;
    }

    String getReason() {
        return reason;
    }

    String getActor() {
        return actor;
    }

    Instant getOccurredAt() {
        return occurredAt;
    }
}
