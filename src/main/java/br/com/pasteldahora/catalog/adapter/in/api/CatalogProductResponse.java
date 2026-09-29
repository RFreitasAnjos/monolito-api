package br.com.pasteldahora.catalog.adapter.in.api;

import br.com.pasteldahora.catalog.domain.model.Category;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CatalogProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String currency,
        CatalogCategoryResponse category,
        InventoryPolicy inventoryPolicy,
        boolean stockControlled,
        boolean customizable,
        List<CatalogIngredientResponse> ingredients
) {

    static CatalogProductResponse from(
            Product product,
            Category category,
            Map<UUID, Ingredient> ingredientsById
    ) {
        return new CatalogProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getSalePrice(),
                "BRL",
                CatalogCategoryResponse.from(category),
                product.getInventoryPolicy(),
                product.getInventoryPolicy() != InventoryPolicy.NOT_CONTROLLED,
                product.isCustomizable(),
                product.getIngredients().stream()
                        .map(selection -> CatalogIngredientResponse.from(
                                ingredientsById.get(selection.ingredientId()),
                                selection,
                                product.isCustomizable()
                        ))
                        .toList()
        );
    }
}
