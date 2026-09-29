package br.com.pasteldahora.order.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public final class OrderItem {

    private final UUID id;
    private final UUID productId;
    private final String sku;
    private final String productName;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;

    private OrderItem(
            UUID id,
            UUID productId,
            String sku,
            String productName,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do item é obrigatório.");
        this.productId = Objects.requireNonNull(productId, "O produto é obrigatório.");
        this.sku = requireText(sku, "O SKU é obrigatório.", 40);
        this.productName = requireText(productName, "O nome do produto é obrigatório.", 120);
        this.quantity = validateQuantity(quantity);
        this.unitPrice = validateUnitPrice(unitPrice);
    }

    static OrderItem create(
            UUID productId,
            String sku,
            String productName,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        return new OrderItem(
                UUID.randomUUID(),
                productId,
                sku,
                productName,
                quantity,
                unitPrice
        );
    }

    public static OrderItem restore(
            UUID id,
            UUID productId,
            String sku,
            String productName,
            BigDecimal quantity,
            BigDecimal unitPrice
    ) {
        return new OrderItem(id, productId, sku, productName, quantity, unitPrice);
    }

    OrderItem increase(BigDecimal additionalQuantity) {
        return new OrderItem(
                id,
                productId,
                sku,
                productName,
                quantity.add(validateQuantity(additionalQuantity)),
                unitPrice
        );
    }

    private static BigDecimal validateQuantity(BigDecimal value) {
        Objects.requireNonNull(value, "A quantidade é obrigatória.");
        if (value.signum() <= 0 || value.scale() > 3) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero e possuir no máximo três casas decimais."
            );
        }
        return value.setScale(3, RoundingMode.UNNECESSARY);
    }

    private static BigDecimal validateUnitPrice(BigDecimal value) {
        Objects.requireNonNull(value, "O preço unitário é obrigatório.");
        if (value.signum() <= 0 || value.scale() > 2) {
            throw new IllegalArgumentException(
                    "O preço unitário deve ser maior que zero e possuir no máximo duas casas decimais."
            );
        }
        return value.setScale(2, RoundingMode.UNNECESSARY);
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

    public BigDecimal getSubtotal() {
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
