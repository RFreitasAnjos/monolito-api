package br.com.pasteldahora.inventory.application.service;

import br.com.pasteldahora.catalog.application.port.in.CatalogItemQuery;
import br.com.pasteldahora.catalog.application.port.in.StockableCatalogItemData;
import br.com.pasteldahora.catalog.application.port.in.StockableProductData;
import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.inventory.application.port.in.CreateStockItemCommand;
import br.com.pasteldahora.inventory.application.port.in.InventoryItemData;
import br.com.pasteldahora.inventory.application.port.in.InventoryFinancialData;
import br.com.pasteldahora.inventory.application.port.in.InventoryFinancialQuery;
import br.com.pasteldahora.inventory.application.port.in.InventoryUseCase;
import br.com.pasteldahora.inventory.application.port.in.InventorySalesPort;
import br.com.pasteldahora.inventory.application.port.in.RegisterSaleCommand;
import br.com.pasteldahora.inventory.application.port.in.RegisterStockMovementCommand;
import br.com.pasteldahora.inventory.application.port.in.UpdateMinimumStockCommand;
import br.com.pasteldahora.inventory.application.port.out.StockItemRepositoryPort;
import br.com.pasteldahora.inventory.application.port.out.StockMovementRepositoryPort;
import br.com.pasteldahora.inventory.domain.exception.DuplicateStockItemException;
import br.com.pasteldahora.inventory.domain.exception.ProductNotEligibleForStockException;
import br.com.pasteldahora.inventory.domain.exception.StockItemNotFoundException;
import br.com.pasteldahora.inventory.domain.model.StockChange;
import br.com.pasteldahora.inventory.domain.model.StockItem;
import br.com.pasteldahora.inventory.domain.model.StockMovement;
import br.com.pasteldahora.inventory.domain.model.StockMovementType;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class InventoryApplicationService
        implements InventoryUseCase, InventoryFinancialQuery, InventorySalesPort {

    private final StockItemRepositoryPort stockItemRepository;
    private final StockMovementRepositoryPort movementRepository;
    private final CatalogItemQuery catalogItemQuery;
    private final Clock clock;

    public InventoryApplicationService(
            StockItemRepositoryPort stockItemRepository,
            StockMovementRepositoryPort movementRepository,
            CatalogItemQuery catalogItemQuery,
            Clock clock
    ) {
        this.stockItemRepository = stockItemRepository;
        this.movementRepository = movementRepository;
        this.catalogItemQuery = catalogItemQuery;
        this.clock = clock;
    }

    @Override
    @Transactional
    public InventoryItemData createStockItem(CreateStockItemCommand command) {
        StockableCatalogItemData item = requireEligibleItem(
                command.itemId(),
                command.itemType()
        );
        if (stockItemRepository.existsByItemIdAndItemType(item.id(), item.itemType())) {
            throw new DuplicateStockItemException();
        }
        StockItem saved = stockItemRepository.save(StockItem.create(
                item.id(),
                item.itemType(),
                command.minimumQuantity(),
                command.actor(),
                Instant.now(clock)
        ));
        return toData(saved, item);
    }

    @Override
    @Transactional
    public InventoryItemData updateMinimumStock(UpdateMinimumStockCommand command) {
        StockItem item = findStockItem(command.stockItemId());
        StockItem saved = stockItemRepository.save(item.updateMinimumQuantity(
                command.minimumQuantity(),
                command.actor(),
                Instant.now(clock)
        ));
        return toData(
                saved,
                catalogItemQuery.findStockableItem(saved.getItemId(), saved.getItemType())
        );
    }

    @Override
    public InventoryItemData findById(UUID stockItemId) {
        StockItem item = findStockItem(stockItemId);
        return toData(
                item,
                catalogItemQuery.findStockableItem(item.getItemId(), item.getItemType())
        );
    }

    @Override
    public List<InventoryItemData> findAll() {
        return stockItemRepository.findAll().stream()
                .map(item -> toData(
                        item,
                        catalogItemQuery.findStockableItem(
                                item.getItemId(),
                                item.getItemType()
                        )
                ))
                .sorted(Comparator.comparing(InventoryItemData::itemName))
                .toList();
    }

    @Override
    public List<StockableCatalogItemData> findItemsAvailableForStock() {
        return catalogItemQuery.findActiveStockableItems().stream()
                .filter(item -> !stockItemRepository.existsByItemIdAndItemType(
                        item.id(),
                        item.itemType()
                ))
                .toList();
    }

    @Override
    @Transactional
    public StockMovement registerMovement(RegisterStockMovementCommand command) {
        if (command.type() == StockMovementType.SALE_EXIT) {
            throw new IllegalArgumentException(
                    "Saídas por venda devem ser registradas pelo módulo de pedidos."
            );
        }
        StockItem item = findStockItem(command.stockItemId());
        requireEligibleItem(item.getItemId(), item.getItemType());
        StockChange change = item.applyMovement(
                command.type(),
                command.quantity(),
                command.unitPrice(),
                command.reason(),
                command.actor(),
                Instant.now(clock)
        );
        stockItemRepository.save(change.stockItem());
        return movementRepository.save(change.movement());
    }

    @Override
    @Transactional
    public void registerSale(RegisterSaleCommand command) {
        StockItem item = stockItemRepository.findByItemIdAndItemType(
                        command.productId(),
                        CatalogItemType.PRODUCT
                )
                .orElseThrow(() -> new StockItemNotFoundException(command.productId()));
        StockChange change = item.applyMovement(
                StockMovementType.SALE_EXIT,
                command.quantity(),
                command.unitPrice(),
                "Venda do pedido " + command.orderId(),
                command.actor(),
                Instant.now(clock)
        );
        stockItemRepository.save(change.stockItem());
        movementRepository.save(change.movement());
    }

    @Override
    public List<StockMovement> findMovements(UUID stockItemId) {
        findStockItem(stockItemId);
        return movementRepository.findByStockItemId(stockItemId);
    }

    @Override
    public InventoryFinancialData getFinancialData() {
        var movements = movementRepository.findAll();
        return new InventoryFinancialData(
                sumMovementValues(movements, StockMovementType.PURCHASE_ENTRY, false),
                sumMovementValues(movements, StockMovementType.SALE_EXIT, false),
                sumMovementValues(movements, StockMovementType.SALE_EXIT, true),
                stockItemRepository.findAll().stream()
                        .map(StockItem::getInventoryValue)
                        .reduce(java.math.BigDecimal.ZERO.setScale(2), java.math.BigDecimal::add)
        );
    }

    private static java.math.BigDecimal sumMovementValues(
            List<StockMovement> movements,
            StockMovementType type,
            boolean cost
    ) {
        return movements.stream()
                .filter(movement -> movement.getType() == type)
                .map(movement -> cost ? movement.getTotalCost() : movement.getTotalValue())
                .reduce(java.math.BigDecimal.ZERO.setScale(2), java.math.BigDecimal::add);
    }

    private StockItem findStockItem(UUID id) {
        return stockItemRepository.findById(id)
                .orElseThrow(() -> new StockItemNotFoundException(id));
    }

    private StockableCatalogItemData requireEligibleItem(
            UUID itemId,
            CatalogItemType itemType
    ) {
        StockableCatalogItemData item = catalogItemQuery.findStockableItem(itemId, itemType);
        if (!item.active()) {
            throw new ProductNotEligibleForStockException(
                    "O item precisa estar ativo para movimentar estoque."
            );
        }
        if (item.itemType() == CatalogItemType.PRODUCT) {
            StockableProductData product = catalogItemQuery.findStockableProduct(item.id());
            if (product.inventoryPolicy() != InventoryPolicy.DIRECT_STOCK) {
                throw new ProductNotEligibleForStockException(
                        "Apenas produtos com estoque direto podem ser controlados neste módulo."
                );
            }
        }
        return item;
    }

    private StockableProductData requireEligibleProduct(UUID productId) {
        StockableProductData product = catalogItemQuery.findStockableProduct(productId);
        if (!product.active() || product.inventoryPolicy() != InventoryPolicy.DIRECT_STOCK) {
            throw new ProductNotEligibleForStockException(
                    "O produto não está disponível para saída de estoque."
            );
        }
        return product;
    }

    private static InventoryItemData toData(
            StockItem item,
            StockableCatalogItemData catalogItem
    ) {
        return new InventoryItemData(
                item.getId(),
                item.getItemId(),
                item.getItemType(),
                catalogItem.sku(),
                catalogItem.name(),
                catalogItem.stockUnit(),
                item.getCurrentQuantity(),
                item.getMinimumQuantity(),
                item.getAverageUnitCost(),
                item.getInventoryValue(),
                item.isBelowMinimum(),
                item.getVersion()
        );
    }
}
