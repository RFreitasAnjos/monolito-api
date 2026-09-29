package br.com.pasteldahora.inventory.application.port.in;

import java.math.BigDecimal;

public record InventoryFinancialData(
        BigDecimal purchaseTotal,
        BigDecimal salesRevenue,
        BigDecimal costOfGoodsSold,
        BigDecimal inventoryValue
) {
}
