package br.com.pasteldahora.inventory.adapter.in.mvc;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MinimumStockForm {

    @NotNull
    @DecimalMin("0.000")
    @Digits(integer = 16, fraction = 3)
    private BigDecimal minimumQuantity;

    public BigDecimal getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(BigDecimal minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}
