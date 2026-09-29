package br.com.pasteldahora.catalog.adapter.in.api;

import br.com.pasteldahora.catalog.domain.model.Category;

import java.util.UUID;

public record CatalogCategoryResponse(
        UUID id,
        String name
) {

    static CatalogCategoryResponse from(Category category) {
        return new CatalogCategoryResponse(category.getId(), category.getName());
    }
}
