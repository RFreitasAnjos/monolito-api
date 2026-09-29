package br.com.pasteldahora.inventory.config;

import br.com.pasteldahora.catalog.application.port.in.CatalogItemQuery;
import br.com.pasteldahora.inventory.application.port.in.InventoryUseCase;
import br.com.pasteldahora.inventory.application.port.out.StockItemRepositoryPort;
import br.com.pasteldahora.inventory.application.port.out.StockMovementRepositoryPort;
import br.com.pasteldahora.inventory.application.service.InventoryApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class InventoryModuleConfig {

    @Bean
    InventoryApplicationService inventoryUseCase(
            StockItemRepositoryPort stockItemRepository,
            StockMovementRepositoryPort movementRepository,
            CatalogItemQuery catalogItemQuery,
            Clock clock
    ) {
        return new InventoryApplicationService(
                stockItemRepository,
                movementRepository,
                catalogItemQuery,
                clock
        );
    }
}
