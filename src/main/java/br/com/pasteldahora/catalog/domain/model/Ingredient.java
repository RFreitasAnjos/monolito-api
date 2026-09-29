package br.com.pasteldahora.catalog.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Ingredient {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9._-]*$");

    private final UUID id;
    private final String sku;
    private final String name;
    private final String description;
    private final UnitOfMeasure stockUnit;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final Instant deactivatedAt;
    private final String deactivatedBy;
    private final long version;

    private Ingredient(
            UUID id,
            String sku,
            String name,
            String description,
            UnitOfMeasure stockUnit,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do insumo é obrigatório.");
        this.sku = validateSku(sku);
        this.name = validateText(name, "O nome do insumo é obrigatório.", 120);
        this.description = normalizeOptional(description, 500);
        this.stockUnit = Objects.requireNonNull(
                stockUnit,
                "A unidade de estoque do insumo é obrigatória."
        ).baseUnit();
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = validateUpdatedAt(createdAt, updatedAt);
        this.createdBy = validateText(createdBy, "O responsável pela criação é obrigatório.", 160);
        this.updatedBy = validateText(updatedBy, "O responsável pela atualização é obrigatório.", 160);
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = validateDeactivation(active, createdAt, updatedAt, deactivatedAt, deactivatedBy);
        if (version < 0) {
            throw new IllegalArgumentException("A versão do insumo não pode ser negativa.");
        }
        this.version = version;
    }

    public static Ingredient create(
            String sku,
            String name,
            String description,
            UnitOfMeasure stockUnit,
            String actor,
            Instant now
    ) {
        return new Ingredient(
                UUID.randomUUID(), sku, name, description, stockUnit, true,
                now, now, actor, actor, null, null, 0
        );
    }

    public static Ingredient restore(
            UUID id,
            String sku,
            String name,
            String description,
            UnitOfMeasure stockUnit,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        return new Ingredient(
                id, sku, name, description, stockUnit, active, createdAt, updatedAt,
                createdBy, updatedBy, deactivatedAt, deactivatedBy, version
        );
    }

    public Ingredient update(
            String sku,
            String name,
            String description,
            UnitOfMeasure stockUnit,
            String actor,
            Instant now
    ) {
        return new Ingredient(
                id, sku, name, description, stockUnit, active, createdAt, now,
                createdBy, actor, deactivatedAt, deactivatedBy, version
        );
    }

    public Ingredient deactivate(String actor, Instant now) {
        if (!active) {
            return this;
        }
        return new Ingredient(
                id, sku, name, description, stockUnit, false, createdAt, now,
                createdBy, actor, now, actor, version
        );
    }

    public Ingredient reactivate(String actor, Instant now) {
        if (active) {
            return this;
        }
        return new Ingredient(
                id, sku, name, description, stockUnit, true, createdAt, now,
                createdBy, actor, null, null, version
        );
    }

    private static String validateSku(String sku) {
        String normalized = validateText(sku, "O SKU do insumo é obrigatório.", 40)
                .toUpperCase(Locale.ROOT);
        if (!SKU_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("O SKU do insumo possui formato inválido.");
        }
        return normalized;
    }

    private static String validateText(String value, String message, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    "O texto informado deve possuir no máximo " + maxLength + " caracteres."
            );
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    "O texto informado deve possuir no máximo " + maxLength + " caracteres."
            );
        }
        return normalized;
    }

    private static Instant validateUpdatedAt(Instant createdAt, Instant updatedAt) {
        Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("A atualização não pode ser anterior à criação.");
        }
        return updatedAt;
    }

    private static String validateDeactivation(
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            Instant deactivatedAt,
            String deactivatedBy
    ) {
        if (active) {
            if (deactivatedAt != null || deactivatedBy != null) {
                throw new IllegalArgumentException("Um insumo ativo não pode possuir dados de desativação.");
            }
            return null;
        }
        Objects.requireNonNull(deactivatedAt, "A data de desativação é obrigatória.");
        if (deactivatedAt.isBefore(createdAt) || deactivatedAt.isAfter(updatedAt)) {
            throw new IllegalArgumentException("A data de desativação é inconsistente.");
        }
        return validateText(
                deactivatedBy,
                "O responsável pela desativação é obrigatório.",
                160
        );
    }

    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UnitOfMeasure getStockUnit() {
        return stockUnit;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    public long getVersion() {
        return version;
    }
}
