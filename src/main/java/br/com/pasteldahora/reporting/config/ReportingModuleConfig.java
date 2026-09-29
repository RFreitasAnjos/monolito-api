package br.com.pasteldahora.reporting.config;

import br.com.pasteldahora.inventory.application.port.in.InventoryFinancialQuery;
import br.com.pasteldahora.order.application.port.in.OrderFinancialQuery;
import br.com.pasteldahora.reporting.application.port.in.ManagementReportQuery;
import br.com.pasteldahora.reporting.application.service.ReportingApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ReportingModuleConfig {

    @Bean
    ManagementReportQuery managementReportQuery(
            InventoryFinancialQuery inventoryFinancialQuery,
            OrderFinancialQuery orderFinancialQuery
    ) {
        return new ReportingApplicationService(inventoryFinancialQuery, orderFinancialQuery);
    }
}
