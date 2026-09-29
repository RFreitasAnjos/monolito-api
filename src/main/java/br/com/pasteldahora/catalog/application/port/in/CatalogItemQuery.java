package br.com.pasteldahora.catalog.application.port.in;

import java.util.UUID;
import java.util.List;
import br.com.pasteldahora.catalog.domain.model.CatalogItemType;

/**
 * Contrato público do Catalog para consultas de outros módulos.
 */
public interface CatalogItemQuery {

    StockableProductData findStockableProduct(UUID productId);

    List<StockableProductData> findActiveDirectStockProducts();

    StockableCatalogItemData findStockableItem(UUID itemId, CatalogItemType itemType);

    List<StockableCatalogItemData> findActiveStockableItems();

    SellableProductData findSellableProduct(UUID productId);

    List<SellableProductData> findActiveSellableProducts();
}
