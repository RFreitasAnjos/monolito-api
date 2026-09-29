package br.com.pasteldahora.order.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record AddOrderItemCommand(
        UUID orderId,
        UUID productId,
        BigDecimal quantity,
        String actor
) {
}
