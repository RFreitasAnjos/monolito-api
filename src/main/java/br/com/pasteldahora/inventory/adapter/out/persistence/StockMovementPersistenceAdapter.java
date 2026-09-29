package br.com.pasteldahora.inventory.adapter.out.persistence;

import br.com.pasteldahora.inventory.application.port.out.StockMovementRepositoryPort;
import br.com.pasteldahora.inventory.domain.model.StockMovement;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class StockMovementPersistenceAdapter implements StockMovementRepositoryPort {

    private final SpringDataStockMovementRepository repository;

    StockMovementPersistenceAdapter(SpringDataStockMovementRepository repository) {
        this.repository = repository;
    }

    @Override
    public StockMovement save(StockMovement movement) {
        return toDomain(repository.save(toEntity(movement)));
    }

    @Override
    public List<StockMovement> findByStockItemId(UUID stockItemId) {
        return repository.findByStockItemIdOrderByOccurredAtDesc(stockItemId)
                .stream()
                .map(StockMovementPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public List<StockMovement> findAll() {
        return repository.findAll().stream()
                .map(StockMovementPersistenceAdapter::toDomain)
                .toList();
    }

    private static StockMovementJpaEntity toEntity(StockMovement movement) {
        return new StockMovementJpaEntity(
                movement.getId(),
                movement.getStockItemId(),
                movement.getType(),
                movement.getQuantity(),
                movement.getResultingBalance(),
                movement.getUnitPrice(),
                movement.getUnitCost(),
                movement.getReason(),
                movement.getActor(),
                movement.getOccurredAt()
        );
    }

    private static StockMovement toDomain(StockMovementJpaEntity entity) {
        return StockMovement.restore(
                entity.getId(),
                entity.getStockItemId(),
                entity.getType(),
                entity.getQuantity(),
                entity.getResultingBalance(),
                entity.getUnitPrice(),
                entity.getUnitCost(),
                entity.getReason(),
                entity.getActor(),
                entity.getOccurredAt()
        );
    }
}
