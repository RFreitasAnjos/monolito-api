package br.com.pasteldahora.inventory.application.port.out;

import br.com.pasteldahora.inventory.domain.model.StockMovement;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepositoryPort {

    StockMovement save(StockMovement movement);

    List<StockMovement> findByStockItemId(UUID stockItemId);

    List<StockMovement> findAll();
}
