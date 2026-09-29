package br.com.pasteldahora.reporting.application.service;

import br.com.pasteldahora.inventory.application.port.in.InventoryFinancialQuery;
import br.com.pasteldahora.order.application.port.in.OrderFinancialQuery;
import br.com.pasteldahora.reporting.application.port.in.ManagementDashboardData;
import br.com.pasteldahora.reporting.application.port.in.ManagementReportQuery;

public final class ReportingApplicationService implements ManagementReportQuery {

    private final InventoryFinancialQuery inventoryFinancialQuery;
    private final OrderFinancialQuery orderFinancialQuery;

    public ReportingApplicationService(
            InventoryFinancialQuery inventoryFinancialQuery,
            OrderFinancialQuery orderFinancialQuery
    ) {
        this.inventoryFinancialQuery = inventoryFinancialQuery;
        this.orderFinancialQuery = orderFinancialQuery;
    }

    @Override
    public ManagementDashboardData getDashboard() {
        var financialData = inventoryFinancialQuery.getFinancialData();
        var orderFinancialData = orderFinancialQuery.getFinancialData();
        return ManagementDashboardData.of(
                financialData.purchaseTotal(),
                orderFinancialData.salesRevenue(),
                financialData.costOfGoodsSold(),
                financialData.inventoryValue()
        );
    }
}
