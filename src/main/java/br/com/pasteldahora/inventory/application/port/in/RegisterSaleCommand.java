package br.com.pasteldahora.inventory.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterSaleCommand(
        UUID productId,
        UUID orderId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String actor
) {
}
