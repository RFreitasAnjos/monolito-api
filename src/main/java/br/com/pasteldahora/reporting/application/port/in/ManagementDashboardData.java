package br.com.pasteldahora.reporting.application.port.in;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ManagementDashboardData(
        BigDecimal purchaseTotal,
        BigDecimal salesRevenue,
        BigDecimal costOfGoodsSold,
        BigDecimal grossProfit,
        BigDecimal grossMarginPercentage,
        BigDecimal inventoryValue
) {

    public static ManagementDashboardData of(
            BigDecimal purchaseTotal,
            BigDecimal salesRevenue,
            BigDecimal costOfGoodsSold,
            BigDecimal inventoryValue
    ) {
        BigDecimal grossProfit = salesRevenue.subtract(costOfGoodsSold);
        BigDecimal grossMargin = salesRevenue.signum() == 0
                ? BigDecimal.ZERO.setScale(2)
                : grossProfit.multiply(new BigDecimal("100"))
                        .divide(salesRevenue, 2, RoundingMode.HALF_UP);
        return new ManagementDashboardData(
                purchaseTotal,
                salesRevenue,
                costOfGoodsSold,
                grossProfit,
                grossMargin,
                inventoryValue
        );
    }
}
