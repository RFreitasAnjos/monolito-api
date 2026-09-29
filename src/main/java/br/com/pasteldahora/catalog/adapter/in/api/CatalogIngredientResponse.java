package br.com.pasteldahora.catalog.adapter.in.api;

import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;

import java.math.BigDecimal;
import java.util.UUID;

public record CatalogIngredientResponse(
        UUID id,
        String sku,
        String name,
        String description,
        UnitOfMeasure stockUnit,
        BigDecimal quantity,
        boolean includedByDefault,
        boolean customerSelectable
) {

    static CatalogIngredientResponse from(
            Ingredient ingredient,
            ProductIngredient selection,
            boolean customizable
    ) {
        return new CatalogIngredientResponse(
                ingredient.getId(),
                ingredient.getSku(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getStockUnit(),
                selection == null ? null : selection.quantity(),
                selection != null && !customizable,
                selection != null && customizable
        );
    }

    static CatalogIngredientResponse available(Ingredient ingredient) {
        return from(ingredient, null, false);
    }
}
