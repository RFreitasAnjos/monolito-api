package br.com.pasteldahora.inventory.adapter.out.persistence;

import br.com.pasteldahora.inventory.application.port.out.StockItemRepositoryPort;
import br.com.pasteldahora.inventory.domain.model.StockItem;
import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StockItemPersistenceAdapter implements StockItemRepositoryPort {

    private final SpringDataStockItemRepository repository;

    StockItemPersistenceAdapter(SpringDataStockItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public StockItem save(StockItem stockItem) {
        return toDomain(repository.save(toEntity(stockItem)));
    }

    @Override
    public Optional<StockItem> findById(UUID id) {
        return repository.findById(id).map(StockItemPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<StockItem> findByItemIdAndItemType(
            UUID itemId,
            CatalogItemType itemType
    ) {
        return repository.findByItemIdAndItemType(itemId, itemType)
                .map(StockItemPersistenceAdapter::toDomain);
    }

    @Override
    public List<StockItem> findAll() {
        return repository.findAllByOrderByCreatedAtAsc().stream()
                .map(StockItemPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public boolean existsByItemIdAndItemType(UUID itemId, CatalogItemType itemType) {
        return repository.existsByItemIdAndItemType(itemId, itemType);
    }

    private static StockItemJpaEntity toEntity(StockItem item) {
        return new StockItemJpaEntity(
                item.getId(),
                item.getItemId(),
                item.getItemType(),
                item.getCurrentQuantity(),
                item.getMinimumQuantity(),
                item.getAverageUnitCost(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getCreatedBy(),
                item.getUpdatedBy(),
                item.getVersion()
        );
    }

    private static StockItem toDomain(StockItemJpaEntity entity) {
        return StockItem.restore(
                entity.getId(),
                entity.getItemId(),
                entity.getItemType(),
                entity.getCurrentQuantity(),
                entity.getMinimumQuantity(),
                entity.getAverageUnitCost(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getVersion()
        );
    }
}
