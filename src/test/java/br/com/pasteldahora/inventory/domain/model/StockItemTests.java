package br.com.pasteldahora.inventory.domain.model;

import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import br.com.pasteldahora.inventory.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockItemTests {

    @Test
    void shouldApplyEntriesAndExitsWithMovementHistoryData() {
        Instant createdAt = Instant.parse("2026-09-29T12:00:00Z");
        StockItem item = StockItem.create(
                UUID.randomUUID(),
                CatalogItemType.PRODUCT,
                new BigDecimal("5"),
                "admin@pasteldahora.local",
                createdAt
        );

        StockChange entry = item.applyMovement(
                StockMovementType.PURCHASE_ENTRY,
                new BigDecimal("10"),
                new BigDecimal("5.00"),
                "Compra do fornecedor",
                "manager@pasteldahora.local",
                createdAt.plusSeconds(60)
        );
        StockChange exit = entry.stockItem().applyMovement(
                StockMovementType.SALE_EXIT,
                new BigDecimal("3"),
                new BigDecimal("9.00"),
                "Venda",
                "manager@pasteldahora.local",
                createdAt.plusSeconds(120)
        );

        assertEquals(new BigDecimal("10.000"), entry.stockItem().getCurrentQuantity());
        assertEquals(new BigDecimal("5.0000"), entry.stockItem().getAverageUnitCost());
        assertEquals(new BigDecimal("50.00"), entry.stockItem().getInventoryValue());
        assertEquals(new BigDecimal("10.000"), entry.movement().getResultingBalance());
        assertEquals(new BigDecimal("7.000"), exit.stockItem().getCurrentQuantity());
        assertEquals(new BigDecimal("5.0000"), exit.stockItem().getAverageUnitCost());
        assertEquals(new BigDecimal("15.00"), exit.movement().getTotalCost());
        assertEquals(new BigDecimal("27.00"), exit.movement().getTotalValue());
        assertEquals(new BigDecimal("7.000"), exit.movement().getResultingBalance());
        assertEquals(StockMovementType.SALE_EXIT, exit.movement().getType());
    }

    @Test
    void shouldRejectExitAboveAvailableBalance() {
        Instant now = Instant.parse("2026-09-29T12:00:00Z");
        StockItem item = StockItem.create(
                UUID.randomUUID(),
                CatalogItemType.PRODUCT,
                BigDecimal.ZERO,
                "admin@pasteldahora.local",
                now
        );

        assertThrows(InsufficientStockException.class, () -> item.applyMovement(
                StockMovementType.MANUAL_EXIT,
                BigDecimal.ONE,
                null,
                "Ajuste",
                "admin@pasteldahora.local",
                now.plusSeconds(60)
        ));
    }

    @Test
    void shouldRecalculateWeightedAverageCostAndFreezeSaleCost() {
        Instant now = Instant.parse("2026-09-29T12:00:00Z");
        StockItem item = StockItem.create(
                UUID.randomUUID(),
                CatalogItemType.PRODUCT,
                BigDecimal.ZERO,
                "admin@pasteldahora.local",
                now
        );

        StockChange firstPurchase = item.applyMovement(
                StockMovementType.PURCHASE_ENTRY,
                new BigDecimal("10"),
                new BigDecimal("5.00"),
                "Primeira compra",
                "admin@pasteldahora.local",
                now.plusSeconds(60)
        );
        StockChange secondPurchase = firstPurchase.stockItem().applyMovement(
                StockMovementType.PURCHASE_ENTRY,
                new BigDecimal("10"),
                new BigDecimal("7.00"),
                "Segunda compra",
                "admin@pasteldahora.local",
                now.plusSeconds(120)
        );
        StockChange sale = secondPurchase.stockItem().applyMovement(
                StockMovementType.SALE_EXIT,
                new BigDecimal("5"),
                new BigDecimal("10.00"),
                "Venda",
                "admin@pasteldahora.local",
                now.plusSeconds(180)
        );

        assertEquals(
                new BigDecimal("6.0000"),
                secondPurchase.stockItem().getAverageUnitCost()
        );
        assertEquals(new BigDecimal("120.00"), secondPurchase.stockItem().getInventoryValue());
        assertEquals(new BigDecimal("30.00"), sale.movement().getTotalCost());
        assertEquals(new BigDecimal("50.00"), sale.movement().getTotalValue());
        assertEquals(new BigDecimal("90.00"), sale.stockItem().getInventoryValue());
    }

    @Test
    void shouldRequireUnitPriceOnlyForPurchasesAndSales() {
        Instant now = Instant.parse("2026-09-29T12:00:00Z");
        StockItem item = StockItem.create(
                UUID.randomUUID(),
                CatalogItemType.PRODUCT,
                BigDecimal.ZERO,
                "admin@pasteldahora.local",
                now
        );

        assertThrows(NullPointerException.class, () -> item.applyMovement(
                StockMovementType.PURCHASE_ENTRY,
                BigDecimal.ONE,
                null,
                "Compra",
                "admin@pasteldahora.local",
                now.plusSeconds(60)
        ));
        assertThrows(IllegalArgumentException.class, () -> item.applyMovement(
                StockMovementType.MANUAL_ENTRY,
                BigDecimal.ONE,
                new BigDecimal("5.00"),
                "Ajuste",
                "admin@pasteldahora.local",
                now.plusSeconds(60)
        ));
    }

    @Test
    void shouldIdentifyBalanceAtOrBelowMinimum() {
        StockItem item = StockItem.create(
                UUID.randomUUID(),
                CatalogItemType.PRODUCT,
                new BigDecimal("5"),
                "admin@pasteldahora.local",
                Instant.parse("2026-09-29T12:00:00Z")
        );

        assertTrue(item.isBelowMinimum());
    }
}
