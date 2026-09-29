package br.com.pasteldahora.catalog.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public record ProductIngredient(
        UUID ingredientId,
        BigDecimal quantity
) {

    public ProductIngredient {
        Objects.requireNonNull(ingredientId, "O ingrediente é obrigatório.");
        Objects.requireNonNull(quantity, "A quantidade do ingrediente é obrigatória.");
        if (quantity.signum() <= 0 || quantity.scale() > 3) {
            throw new IllegalArgumentException(
                    "A quantidade do ingrediente deve ser maior que zero "
                            + "e possuir no máximo três casas decimais."
            );
        }
        quantity = quantity.setScale(3, RoundingMode.UNNECESSARY);
    }
}
