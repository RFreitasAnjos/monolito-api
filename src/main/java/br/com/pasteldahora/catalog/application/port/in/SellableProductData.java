package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;

import java.math.BigDecimal;
import java.util.UUID;

public record SellableProductData(
        UUID id,
        String sku,
        String name,
        BigDecimal salePrice,
        InventoryPolicy inventoryPolicy,
        boolean active
) {
}
