package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;

public record CreateIngredientCommand(
        String sku,
        String name,
        String description,
        UnitOfMeasure stockUnit,
        String actor
) {
}
