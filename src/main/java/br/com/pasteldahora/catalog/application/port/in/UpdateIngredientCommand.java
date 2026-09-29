package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;

import java.util.UUID;

public record UpdateIngredientCommand(
        UUID id,
        String sku,
        String name,
        String description,
        UnitOfMeasure stockUnit,
        String actor
) {
}
