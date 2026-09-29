package br.com.pasteldahora.inventory.application.port.in;

import br.com.pasteldahora.catalog.application.port.in.StockableCatalogItemData;
import br.com.pasteldahora.inventory.domain.model.StockMovement;

import java.util.List;
import java.util.UUID;

public interface InventoryUseCase {

    InventoryItemData createStockItem(CreateStockItemCommand command);

    InventoryItemData updateMinimumStock(UpdateMinimumStockCommand command);

    InventoryItemData findById(UUID stockItemId);

    List<InventoryItemData> findAll();

    List<StockableCatalogItemData> findItemsAvailableForStock();

    StockMovement registerMovement(RegisterStockMovementCommand command);

    List<StockMovement> findMovements(UUID stockItemId);
}
