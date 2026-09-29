package br.com.pasteldahora.inventory.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

final class StockQuantity {

    private static final int SCALE = 3;

    private StockQuantity() {
    }

    static BigDecimal positive(BigDecimal value, String fieldName) {
        BigDecimal normalized = normalize(value, fieldName);
        if (normalized.signum() <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
        }
        return normalized;
    }

    static BigDecimal nonNegative(BigDecimal value, String fieldName) {
        BigDecimal normalized = normalize(value, fieldName);
        if (normalized.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser negativo.");
        }
        return normalized;
    }

    private static BigDecimal normalize(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " é obrigatório.");
        if (value.scale() > SCALE) {
            throw new IllegalArgumentException(
                    fieldName + " deve possuir no máximo três casas decimais."
            );
        }
        return value.setScale(SCALE, RoundingMode.UNNECESSARY);
    }
}
