package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductCommand(
        String sku,
        String name,
        String description,
        UUID categoryId,
        BigDecimal salePrice,
        InventoryPolicy inventoryPolicy,
        boolean customizable,
        List<ProductIngredient> ingredients,
        String actor
) {
}
