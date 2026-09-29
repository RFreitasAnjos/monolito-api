package br.com.pasteldahora.catalog.domain.model;

import java.math.BigDecimal;

public enum UnitOfMeasure {

    UNIT("Unidade", MeasurementDimension.COUNT, BigDecimal.ONE),
    GRAM("Grama", MeasurementDimension.MASS, BigDecimal.ONE),
    KILOGRAM("Quilograma", MeasurementDimension.MASS, new BigDecimal("1000")),
    MILLILITER("Mililitro", MeasurementDimension.VOLUME, BigDecimal.ONE),
    LITER("Litro", MeasurementDimension.VOLUME, new BigDecimal("1000"));

    private final String description;
    private final MeasurementDimension dimension;
    private final BigDecimal baseFactor;

    UnitOfMeasure(
            String description,
            MeasurementDimension dimension,
            BigDecimal baseFactor
    ) {
        this.description = description;
        this.dimension = dimension;
        this.baseFactor = baseFactor;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompatibleWith(UnitOfMeasure other) {
        return other != null && dimension == other.dimension;
    }

    public BigDecimal toBaseUnit(BigDecimal quantity) {
        return quantity.multiply(baseFactor);
    }

    public UnitOfMeasure baseUnit() {
        return switch (dimension) {
            case COUNT -> UNIT;
            case MASS -> GRAM;
            case VOLUME -> MILLILITER;
        };
    }

    private enum MeasurementDimension {
        COUNT,
        MASS,
        VOLUME
    }
}
