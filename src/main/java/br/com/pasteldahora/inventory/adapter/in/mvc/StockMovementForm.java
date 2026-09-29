package br.com.pasteldahora.inventory.adapter.in.mvc;

import br.com.pasteldahora.inventory.domain.model.StockMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class StockMovementForm {

    @NotNull
    private StockMovementType type;

    @NotNull
    @DecimalMin("0.001")
    @Digits(integer = 16, fraction = 3)
    private BigDecimal quantity;

    @DecimalMin("0.01")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal unitPrice;

    @NotBlank
    @Size(max = 250)
    private String reason;

    public StockMovementType getType() {
        return type;
    }

    public void setType(StockMovementType type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
