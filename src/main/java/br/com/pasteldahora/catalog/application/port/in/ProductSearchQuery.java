package br.com.pasteldahora.catalog.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSearchQuery(
        String term,
        Boolean active,
        UUID categoryId,
        BigDecimal minimumPrice,
        BigDecimal maximumPrice,
        ProductSort sort,
        int page,
        int size
) {

    public ProductSearchQuery {
        term = term == null ? "" : term.trim();
        sort = sort == null ? ProductSort.NAME_ASC : sort;
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 100);
        if (minimumPrice != null && minimumPrice.signum() < 0) {
            throw new IllegalArgumentException("O preço mínimo não pode ser negativo.");
        }
        if (maximumPrice != null && maximumPrice.signum() < 0) {
            throw new IllegalArgumentException("O preço máximo não pode ser negativo.");
        }
        if (minimumPrice != null
                && maximumPrice != null
                && minimumPrice.compareTo(maximumPrice) > 0) {
            throw new IllegalArgumentException(
                    "O preço mínimo não pode ser maior que o preço máximo."
            );
        }
    }
}
