package br.com.pasteldahora.inventory.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataStockMovementRepository
        extends JpaRepository<StockMovementJpaEntity, UUID> {

    List<StockMovementJpaEntity> findByStockItemIdOrderByOccurredAtDesc(UUID stockItemId);
}
