package br.com.pasteldahora.inventory.application.port.in;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateStockItemCommand(
        UUID itemId,
        CatalogItemType itemType,
        BigDecimal minimumQuantity,
        String actor
) {
}
