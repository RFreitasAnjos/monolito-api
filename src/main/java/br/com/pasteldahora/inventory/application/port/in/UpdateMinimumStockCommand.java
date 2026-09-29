package br.com.pasteldahora.inventory.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMinimumStockCommand(
        UUID stockItemId,
        BigDecimal minimumQuantity,
        String actor
) {
}
