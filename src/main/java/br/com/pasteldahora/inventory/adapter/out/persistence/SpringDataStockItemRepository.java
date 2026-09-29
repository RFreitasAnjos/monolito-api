package br.com.pasteldahora.inventory.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.pasteldahora.catalog.domain.model.CatalogItemType;

import java.util.List;
import java.util.UUID;

interface SpringDataStockItemRepository extends JpaRepository<StockItemJpaEntity, UUID> {

    List<StockItemJpaEntity> findAllByOrderByCreatedAtAsc();

    boolean existsByItemIdAndItemType(UUID itemId, CatalogItemType itemType);

    java.util.    Optional<StockItemJpaEntity> findByItemIdAndItemType(UUID itemId, CatalogItemType itemType);
}
