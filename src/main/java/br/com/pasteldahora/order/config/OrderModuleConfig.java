package br.com.pasteldahora.order.config;

import br.com.pasteldahora.catalog.application.port.in.CatalogItemQuery;
import br.com.pasteldahora.inventory.application.port.in.InventorySalesPort;
import br.com.pasteldahora.order.application.port.out.OrderRepositoryPort;
import br.com.pasteldahora.order.application.service.OrderApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class OrderModuleConfig {

    @Bean
    OrderApplicationService orderUseCase(
            OrderRepositoryPort orderRepository,
            CatalogItemQuery catalogItemQuery,
            InventorySalesPort inventorySalesPort,
            Clock clock
    ) {
        return new OrderApplicationService(
                orderRepository,
                catalogItemQuery,
                inventorySalesPort,
                clock
        );
    }
}
