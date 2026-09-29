package br.com.pasteldahora.order.adapter.in.mvc;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class AddOrderItemForm {

    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin("0.001")
    @Digits(integer = 16, fraction = 3)
    private BigDecimal quantity;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
