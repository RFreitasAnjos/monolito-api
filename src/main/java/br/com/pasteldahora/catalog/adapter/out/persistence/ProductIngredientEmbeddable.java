package br.com.pasteldahora.catalog.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
class ProductIngredientEmbeddable {

    @Column(name = "ingredient_id", nullable = false)
    private UUID ingredientId;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantity;

    protected ProductIngredientEmbeddable() {
    }

    ProductIngredientEmbeddable(UUID ingredientId, BigDecimal quantity) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    UUID getIngredientId() {
        return ingredientId;
    }

    BigDecimal getQuantity() {
        return quantity;
    }
}
