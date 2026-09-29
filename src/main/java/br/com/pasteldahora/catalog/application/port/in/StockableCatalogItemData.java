package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;

import java.util.UUID;

public record StockableCatalogItemData(
        UUID id,
        String sku,
        String name,
        CatalogItemType itemType,
        UnitOfMeasure stockUnit,
        boolean active
) {
}
