package br.com.pasteldahora.reporting.application.service;

import br.com.pasteldahora.inventory.application.port.in.InventoryFinancialData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportingApplicationServiceTests {

    @Test
    void shouldCalculateManagementDashboardFromInventoryFinancialData() {
        ReportingApplicationService service = new ReportingApplicationService(
                () -> new InventoryFinancialData(
                        new BigDecimal("120.00"),
                        BigDecimal.ZERO.setScale(2),
                        new BigDecimal("30.00"),
                        new BigDecimal("90.00")
                ),
                () -> new br.com.pasteldahora.order.application.port.in.OrderFinancialData(
                        new BigDecimal("50.00")
                )
        );

        var dashboard = service.getDashboard();

        assertEquals(new BigDecimal("120.00"), dashboard.purchaseTotal());
        assertEquals(new BigDecimal("50.00"), dashboard.salesRevenue());
        assertEquals(new BigDecimal("30.00"), dashboard.costOfGoodsSold());
        assertEquals(new BigDecimal("20.00"), dashboard.grossProfit());
        assertEquals(new BigDecimal("40.00"), dashboard.grossMarginPercentage());
        assertEquals(new BigDecimal("90.00"), dashboard.inventoryValue());
    }
}
