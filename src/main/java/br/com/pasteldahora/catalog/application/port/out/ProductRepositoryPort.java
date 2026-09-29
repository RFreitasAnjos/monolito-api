package br.com.pasteldahora.catalog.application.port.out;

import br.com.pasteldahora.catalog.application.port.in.PageResult;
import br.com.pasteldahora.catalog.application.port.in.ProductSearchQuery;
import br.com.pasteldahora.catalog.domain.model.Product;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    PageResult<Product> search(ProductSearchQuery query);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    boolean existsByCategoryIdAndActiveTrue(UUID categoryId);

    boolean existsByIngredientIdAndActiveTrue(UUID ingredientId);

    List<Product> findActiveByInventoryPolicy(
            br.com.pasteldahora.catalog.domain.model.InventoryPolicy inventoryPolicy
    );

    List<Product> findActive();
}
