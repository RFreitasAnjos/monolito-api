package br.com.pasteldahora.order.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTests {

    private static final String ACTOR = "operator@pasteldahora.local";
    private static final Instant NOW = Instant.parse("2026-09-29T14:00:00Z");

    @Test
    void shouldBuildOrderAndFreezeItemPrice() {
        UUID productId = UUID.randomUUID();
        Order order = Order.create(null, ACTOR, NOW)
                .addItem(
                        productId,
                        "PASTEL-CARNE",
                        "Pastel de carne",
                        new BigDecimal("2"),
                        new BigDecimal("12.50"),
                        ACTOR,
                        NOW.plusSeconds(10)
                )
                .addItem(
                        productId,
                        "PASTEL-CARNE",
                        "Pastel de carne",
                        BigDecimal.ONE,
                        new BigDecimal("15.00"),
                        ACTOR,
                        NOW.plusSeconds(20)
                );

        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("3.000"), order.getItems().getFirst().getQuantity());
        assertEquals(new BigDecimal("12.50"), order.getItems().getFirst().getUnitPrice());
        assertEquals(new BigDecimal("37.50"), order.getTotalAmount());
    }

    @Test
    void shouldCompleteOnlyNonEmptyOpenOrder() {
        Order emptyOrder = Order.create(null, ACTOR, NOW);
        assertThrows(
                IllegalStateException.class,
                () -> emptyOrder.complete(ACTOR, NOW.plusSeconds(10))
        );

        Order completed = emptyOrder
                .addItem(
                        UUID.randomUUID(),
                        "REFRI",
                        "Refrigerante",
                        BigDecimal.ONE,
                        new BigDecimal("6.00"),
                        ACTOR,
                        NOW.plusSeconds(10)
                )
                .complete(ACTOR, NOW.plusSeconds(20));

        assertEquals(OrderStatus.COMPLETED, completed.getStatus());
        assertEquals(new BigDecimal("6.00"), completed.getTotalAmount());
        assertThrows(
                IllegalStateException.class,
                () -> completed.cancel(ACTOR, NOW.plusSeconds(30))
        );
    }

    @Test
    void shouldCancelOpenOrderAndRejectFurtherChanges() {
        Order cancelled = Order.create(null, ACTOR, NOW)
                .cancel(ACTOR, NOW.plusSeconds(10));

        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertThrows(IllegalStateException.class, () -> cancelled.addItem(
                UUID.randomUUID(),
                "SKU",
                "Produto",
                BigDecimal.ONE,
                BigDecimal.ONE,
                ACTOR,
                NOW.plusSeconds(20)
        ));
    }
}
