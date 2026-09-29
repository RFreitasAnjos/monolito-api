package br.com.pasteldahora.catalog.config;

import br.com.pasteldahora.catalog.application.port.in.CategoryUseCase;
import br.com.pasteldahora.catalog.application.port.in.IngredientUseCase;
import br.com.pasteldahora.catalog.application.port.out.CategoryRepositoryPort;
import br.com.pasteldahora.catalog.application.port.out.IngredientRepositoryPort;
import br.com.pasteldahora.catalog.application.port.out.ProductRepositoryPort;
import br.com.pasteldahora.catalog.application.service.CategoryApplicationService;
import br.com.pasteldahora.catalog.application.service.IngredientApplicationService;
import br.com.pasteldahora.catalog.application.service.ProductApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class CatalogModuleConfig {

    @Bean
    ProductApplicationService productApplicationService(
            ProductRepositoryPort productRepository,
            CategoryRepositoryPort categoryRepository,
            IngredientRepositoryPort ingredientRepository,
            Clock clock
    ) {
        return new ProductApplicationService(
                productRepository,
                categoryRepository,
                ingredientRepository,
                clock
        );
    }

    @Bean
    IngredientUseCase ingredientUseCase(
            IngredientRepositoryPort ingredientRepository,
            ProductRepositoryPort productRepository,
            Clock clock
    ) {
        return new IngredientApplicationService(
                ingredientRepository,
                productRepository,
                clock
        );
    }

    @Bean
    CategoryUseCase categoryUseCase(
            CategoryRepositoryPort categoryRepository,
            ProductRepositoryPort productRepository,
            Clock clock
    ) {
        return new CategoryApplicationService(categoryRepository, productRepository, clock);
    }
}
