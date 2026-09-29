package br.com.pasteldahora.inventory.application.port.in;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;
import java.math.BigDecimal;
import java.util.UUID;

public record InventoryItemData(
        UUID stockItemId,
        UUID itemId,
        CatalogItemType itemType,
        String sku,
        String itemName,
        UnitOfMeasure stockUnit,
        BigDecimal currentQuantity,
        BigDecimal minimumQuantity,
        BigDecimal averageUnitCost,
        BigDecimal inventoryValue,
        boolean belowMinimum,
        long version
) {
}
