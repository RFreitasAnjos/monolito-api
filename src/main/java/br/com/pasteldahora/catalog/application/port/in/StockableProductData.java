package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;

import java.util.UUID;

public record StockableProductData(
        UUID id,
        String sku,
        String name,
        InventoryPolicy inventoryPolicy,
        boolean active
) {
}
