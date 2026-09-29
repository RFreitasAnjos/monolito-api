package br.com.pasteldahora.inventory.application.port.in;

import br.com.pasteldahora.inventory.domain.model.StockMovementType;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterStockMovementCommand(
        UUID stockItemId,
        StockMovementType type,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String reason,
        String actor
) {
}
