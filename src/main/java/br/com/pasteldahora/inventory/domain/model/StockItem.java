package br.com.pasteldahora.inventory.domain.model;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import br.com.pasteldahora.inventory.domain.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class StockItem {

    private final UUID id;
    private final UUID itemId;
    private final CatalogItemType itemType;
    private final BigDecimal currentQuantity;
    private final BigDecimal minimumQuantity;
    private final BigDecimal averageUnitCost;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final long version;

    private StockItem(
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
        this.id = Objects.requireNonNull(id, "O identificador do estoque é obrigatório.");
        this.itemId = Objects.requireNonNull(itemId, "O item do estoque é obrigatório.");
        this.itemType = Objects.requireNonNull(itemType, "O tipo do item é obrigatório.");
        this.currentQuantity = StockQuantity.nonNegative(currentQuantity, "O saldo atual");
        this.minimumQuantity = StockQuantity.nonNegative(
                minimumQuantity,
                "O estoque mínimo"
        );
        this.averageUnitCost = validateAverageUnitCost(averageUnitCost);
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = validateUpdatedAt(createdAt, updatedAt);
        this.createdBy = validateActor(createdBy);
        this.updatedBy = validateActor(updatedBy);
        if (version < 0) {
            throw new IllegalArgumentException("A versão do estoque não pode ser negativa.");
        }
        this.version = version;
    }

    public static StockItem create(
            UUID itemId,
            CatalogItemType itemType,
            BigDecimal minimumQuantity,
            String actor,
            Instant now
    ) {
        return new StockItem(
                UUID.randomUUID(),
                itemId,
                itemType,
                BigDecimal.ZERO,
                minimumQuantity,
                BigDecimal.ZERO.setScale(4),
                now,
                now,
                actor,
                actor,
                0
        );
    }

    public static StockItem restore(
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
        return new StockItem(
                id,
                itemId,
                itemType,
                currentQuantity,
                minimumQuantity,
                averageUnitCost,
                createdAt,
                updatedAt,
                createdBy,
                updatedBy,
                version
        );
    }

    public StockItem updateMinimumQuantity(
            BigDecimal minimumQuantity,
            String actor,
            Instant now
    ) {
        return new StockItem(
                id,
                itemId,
                itemType,
                currentQuantity,
                minimumQuantity,
                averageUnitCost,
                createdAt,
                now,
                createdBy,
                actor,
                version
        );
    }

    public StockChange applyMovement(
            StockMovementType type,
            BigDecimal quantity,
            BigDecimal unitPrice,
            String reason,
            String actor,
            Instant now
    ) {
        BigDecimal normalizedQuantity = StockQuantity.positive(
                quantity,
                "A quantidade movimentada"
        );
        BigDecimal resultingBalance = type.isEntry()
                ? currentQuantity.add(normalizedQuantity)
                : currentQuantity.subtract(normalizedQuantity);
        if (resultingBalance.signum() < 0) {
            throw new InsufficientStockException(currentQuantity, normalizedQuantity);
        }
        BigDecimal normalizedUnitPrice = StockMovement.normalizeUnitPrice(type, unitPrice);
        BigDecimal movementUnitCost = type == StockMovementType.PURCHASE_ENTRY
                ? normalizedUnitPrice
                : averageUnitCost;
        BigDecimal resultingAverageUnitCost = calculateResultingAverageUnitCost(
                type,
                normalizedQuantity,
                resultingBalance,
                normalizedUnitPrice
        );

        StockItem updated = new StockItem(
                id,
                itemId,
                itemType,
                resultingBalance,
                minimumQuantity,
                resultingAverageUnitCost,
                createdAt,
                now,
                createdBy,
                actor,
                version
        );
        StockMovement movement = StockMovement.create(
                id,
                type,
                normalizedQuantity,
                resultingBalance,
                normalizedUnitPrice,
                movementUnitCost,
                reason,
                actor,
                now
        );
        return new StockChange(updated, movement);
    }

    public boolean isBelowMinimum() {
        return currentQuantity.compareTo(minimumQuantity) <= 0;
    }

    private BigDecimal calculateResultingAverageUnitCost(
            StockMovementType type,
            BigDecimal quantity,
            BigDecimal resultingBalance,
            BigDecimal unitPrice
    ) {
        if (resultingBalance.signum() == 0) {
            return BigDecimal.ZERO.setScale(4);
        }
        if (type != StockMovementType.PURCHASE_ENTRY) {
            return averageUnitCost;
        }
        BigDecimal currentValue = currentQuantity.multiply(averageUnitCost);
        BigDecimal purchaseValue = quantity.multiply(unitPrice);
        return currentValue.add(purchaseValue)
                .divide(resultingBalance, 4, RoundingMode.HALF_UP);
    }

    private static BigDecimal validateAverageUnitCost(BigDecimal value) {
        Objects.requireNonNull(value, "O custo médio do estoque é obrigatório.");
        if (value.signum() < 0) {
            throw new IllegalArgumentException("O custo médio não pode ser negativo.");
        }
        if (value.scale() > 4) {
            throw new IllegalArgumentException(
                    "O custo médio deve possuir no máximo quatro casas decimais."
            );
        }
        return value.setScale(4, RoundingMode.UNNECESSARY);
    }

    private static Instant validateUpdatedAt(Instant createdAt, Instant updatedAt) {
        Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("A atualização não pode ser anterior à criação.");
        }
        return updatedAt;
    }

    private static String validateActor(String actor) {
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("O responsável pela operação é obrigatório.");
        }
        String normalized = actor.trim();
        if (normalized.length() > 160) {
            throw new IllegalArgumentException(
                    "O responsável deve possuir no máximo 160 caracteres."
            );
        }
        return normalized;
    }

    public UUID getId() {
        return id;
    }

    public UUID getItemId() {
        return itemId;
    }

    public CatalogItemType getItemType() {
        return itemType;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public BigDecimal getMinimumQuantity() {
        return minimumQuantity;
    }

    public BigDecimal getAverageUnitCost() {
        return averageUnitCost;
    }

    public BigDecimal getInventoryValue() {
        return currentQuantity.multiply(averageUnitCost).setScale(2, RoundingMode.HALF_UP);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public long getVersion() {
        return version;
    }
}
