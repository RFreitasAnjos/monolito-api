package br.com.pasteldahora.inventory.application.port.out;

import br.com.pasteldahora.inventory.domain.model.StockItem;
import br.com.pasteldahora.catalog.domain.model.CatalogItemType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepositoryPort {

    StockItem save(StockItem stockItem);

    Optional<StockItem> findById(UUID id);

    Optional<StockItem> findByItemIdAndItemType(UUID itemId, CatalogItemType itemType);

    List<StockItem> findAll();

    boolean existsByItemIdAndItemType(UUID itemId, CatalogItemType itemType);
}
