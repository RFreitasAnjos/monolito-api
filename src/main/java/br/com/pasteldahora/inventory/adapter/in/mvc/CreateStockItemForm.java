package br.com.pasteldahora.inventory.adapter.in.mvc;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateStockItemForm {

    @NotBlank
    private String itemReference;

    @NotNull
    @DecimalMin("0.000")
    @Digits(integer = 16, fraction = 3)
    private BigDecimal minimumQuantity;

    public String getItemReference() {
        return itemReference;
    }

    public void setItemReference(String itemReference) {
        this.itemReference = itemReference;
    }

    UUID itemId() {
        return UUID.fromString(referencePart(1));
    }

    CatalogItemType itemType() {
        return CatalogItemType.valueOf(referencePart(0));
    }

    private String referencePart(int index) {
        if (itemReference == null || !itemReference.matches("^[A-Z]+:[0-9a-fA-F-]{36}$")) {
            throw new IllegalArgumentException("O item de estoque informado é inválido.");
        }
        return itemReference.split(":", 2)[index];
    }

    public BigDecimal getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(BigDecimal minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}
