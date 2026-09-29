package br.com.pasteldahora.inventory.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class StockMovement {

    private final UUID id;
    private final UUID stockItemId;
    private final StockMovementType type;
    private final BigDecimal quantity;
    private final BigDecimal resultingBalance;
    private final BigDecimal unitPrice;
    private final BigDecimal unitCost;
    private final String reason;
    private final String actor;
    private final Instant occurredAt;

    private StockMovement(
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
        this.id = Objects.requireNonNull(id, "O identificador da movimentação é obrigatório.");
        this.stockItemId = Objects.requireNonNull(
                stockItemId,
                "O item de estoque da movimentação é obrigatório."
        );
        this.type = Objects.requireNonNull(type, "O tipo da movimentação é obrigatório.");
        this.quantity = StockQuantity.positive(quantity, "A quantidade movimentada");
        this.resultingBalance = StockQuantity.nonNegative(
                resultingBalance,
                "O saldo resultante"
        );
        this.unitPrice = normalizeUnitPrice(type, unitPrice);
        this.unitCost = normalizeUnitCost(unitCost);
        this.reason = requireText(reason, "O motivo da movimentação é obrigatório.", 250);
        this.actor = requireText(actor, "O responsável pela movimentação é obrigatório.", 160);
        this.occurredAt = Objects.requireNonNull(
                occurredAt,
                "A data da movimentação é obrigatória."
        );
    }

    static StockMovement create(
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
        return new StockMovement(
                UUID.randomUUID(),
                stockItemId,
                type,
                quantity,
                resultingBalance,
                unitPrice,
                unitCost,
                reason,
                actor,
                occurredAt
        );
    }

    public static StockMovement restore(
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
        return new StockMovement(
                id,
                stockItemId,
                type,
                quantity,
                resultingBalance,
                unitPrice,
                unitCost,
                reason,
                actor,
                occurredAt
        );
    }

    private static String requireText(String value, String message, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    "O texto informado deve possuir no máximo " + maxLength + " caracteres."
            );
        }
        return normalized;
    }

    static BigDecimal normalizeUnitPrice(StockMovementType type, BigDecimal value) {
        if (!type.isUnitPriceRequired()) {
            if (value != null) {
                throw new IllegalArgumentException(
                        "O valor unitário só deve ser informado para compras e vendas."
                );
            }
            return null;
        }
        Objects.requireNonNull(value, "O valor unitário é obrigatório para compras e vendas.");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("O valor unitário deve ser maior que zero.");
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException(
                    "O valor unitário deve possuir no máximo duas casas decimais."
            );
        }
        return value.setScale(2, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal normalizeUnitCost(BigDecimal value) {
        Objects.requireNonNull(value, "O custo unitário da movimentação é obrigatório.");
        if (value.signum() < 0 || value.scale() > 4) {
            throw new IllegalArgumentException(
                    "O custo unitário deve ser positivo e possuir no máximo quatro casas decimais."
            );
        }
        return value.setScale(4, RoundingMode.UNNECESSARY);
    }

    public UUID getId() {
        return id;
    }

    public UUID getStockItemId() {
        return stockItemId;
    }

    public StockMovementType getType() {
        return type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getTotalValue() {
        return unitPrice == null
                ? BigDecimal.ZERO.setScale(2)
                : quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalCost() {
        return quantity.multiply(unitCost).setScale(2, RoundingMode.HALF_UP);
    }

    public String getReason() {
        return reason;
    }

    public String getActor() {
        return actor;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
