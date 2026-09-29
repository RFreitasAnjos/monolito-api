package br.com.pasteldahora.catalog.adapter.in.api;

import br.com.pasteldahora.catalog.application.port.in.PageResult;
import br.com.pasteldahora.catalog.domain.model.Category;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CatalogProductPageResponse(
        List<CatalogProductResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    static CatalogProductPageResponse from(
            PageResult<Product> products,
            Map<UUID, Category> categories,
            Map<UUID, Ingredient> ingredients
    ) {
        List<CatalogProductResponse> content = products.content().stream()
                .map(product -> CatalogProductResponse.from(
                        product,
                        categories.get(product.getCategoryId()),
                        ingredients
                ))
                .toList();
        return new CatalogProductPageResponse(
                content,
                products.page(),
                products.size(),
                products.totalElements(),
                products.totalPages(),
                products.page() == 0,
                products.totalPages() == 0 || products.page() + 1 >= products.totalPages()
        );
    }
}
