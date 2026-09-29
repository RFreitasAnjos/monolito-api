package br.com.pasteldahora.catalog.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Locale;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Produto comercializado pela Pastel da Hora.
 *
 * <p>O catálogo define os dados comerciais e a política de estoque do produto.
 * Saldos, reservas e movimentações pertencem exclusivamente ao módulo
 * Inventory.</p>
 */
public final class Product {

    private static final int MAX_SKU_LENGTH = 40;
    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_ACTOR_LENGTH = 160;
    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9._-]*$");

    private final UUID id;
    private final String sku;
    private final String name;
    private final String description;
    private final UUID categoryId;
    private final BigDecimal salePrice;
    private final InventoryPolicy inventoryPolicy;
    private final boolean customizable;
    private final List<ProductIngredient> ingredients;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final Instant deactivatedAt;
    private final String deactivatedBy;
    private final long version;

    private Product(
            UUID id,
            String sku,
            String name,
            String description,
            UUID categoryId,
            BigDecimal salePrice,
            InventoryPolicy inventoryPolicy,
            boolean customizable,
            List<ProductIngredient> ingredients,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do produto é obrigatório.");
        this.sku = validateSku(sku);
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.categoryId = Objects.requireNonNull(
                categoryId,
                "A categoria do produto é obrigatória."
        );
        this.salePrice = validateSalePrice(salePrice);
        this.inventoryPolicy = Objects.requireNonNull(
                inventoryPolicy,
                "A política de estoque é obrigatória."
        );
        this.customizable = customizable;
        this.ingredients = validateIngredients(ingredients);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = validateUpdatedAt(createdAt, updatedAt);
        this.createdBy = validateActor(createdBy, "O responsável pela criação é obrigatório.");
        this.updatedBy = validateActor(updatedBy, "O responsável pela atualização é obrigatório.");
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = validateDeactivation(
                active,
                this.createdAt,
                this.updatedAt,
                deactivatedAt,
                deactivatedBy
        );
        this.version = validateVersion(version);
    }

    /**
     * Cadastra um produto ativo, com identidade e informações de auditoria
     * próprias. A existência e unicidade da categoria e do SKU são verificadas
     * pelo serviço de aplicação.
     */
    public static Product create(
            String sku,
            String name,
            String description,
            UUID categoryId,
            BigDecimal salePrice,
            InventoryPolicy inventoryPolicy,
            boolean customizable,
            List<ProductIngredient> ingredients,
            String actor,
            Instant now
    ) {
        return new Product(
                UUID.randomUUID(),
                sku,
                name,
                description,
                categoryId,
                salePrice,
                inventoryPolicy,
                customizable,
                ingredients,
                true,
                now,
                now,
                actor,
                actor,
                null,
                null,
                0
        );
    }

    /**
     * Reconstrói um produto persistido sem gerar nova identidade ou alterar sua
     * versão.
     */
    public static Product restore(
            UUID id,
            String sku,
            String name,
            String description,
            UUID categoryId,
            BigDecimal salePrice,
            InventoryPolicy inventoryPolicy,
            boolean customizable,
            List<ProductIngredient> ingredients,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        return new Product(
                id,
                sku,
                name,
                description,
                categoryId,
                salePrice,
                inventoryPolicy,
                customizable,
                ingredients,
                active,
                createdAt,
                updatedAt,
                createdBy,
                updatedBy,
                deactivatedAt,
                deactivatedBy,
                version
        );
    }

    /**
     * Atualiza os dados comerciais sem alterar identidade, status ou histórico
     * de criação.
     */
    public Product update(
            String sku,
            String name,
            String description,
            UUID categoryId,
            BigDecimal salePrice,
            InventoryPolicy inventoryPolicy,
            boolean customizable,
            List<ProductIngredient> ingredients,
            String actor,
            Instant now
    ) {
        return new Product(
                id,
                sku,
                name,
                description,
                categoryId,
                salePrice,
                inventoryPolicy,
                customizable,
                ingredients,
                active,
                createdAt,
                now,
                createdBy,
                actor,
                deactivatedAt,
                deactivatedBy,
                version
        );
    }

    public Product deactivate(String actor, Instant now) {
        if (!active) {
            return this;
        }
        return new Product(
                id,
                sku,
                name,
                description,
                categoryId,
                salePrice,
                inventoryPolicy,
                customizable,
                ingredients,
                false,
                createdAt,
                now,
                createdBy,
                actor,
                now,
                actor,
                version
        );
    }

    public Product reactivate(String actor, Instant now) {
        if (active) {
            return this;
        }
        return new Product(
                id,
                sku,
                name,
                description,
                categoryId,
                salePrice,
                inventoryPolicy,
                customizable,
                ingredients,
                true,
                createdAt,
                now,
                createdBy,
                actor,
                null,
                null,
                version
        );
    }

    private static String validateSku(String sku) {
        String normalizedSku = requireText(sku, "O SKU do produto é obrigatório.")
                .trim()
                .toUpperCase(Locale.ROOT);
        if (normalizedSku.length() > MAX_SKU_LENGTH || !SKU_PATTERN.matcher(normalizedSku).matches()) {
            throw new IllegalArgumentException(
                    "O SKU deve possuir no máximo 40 caracteres e usar apenas letras, números, ponto, hífen ou sublinhado."
            );
        }
        return normalizedSku;
    }

    private static String validateName(String name) {
        String normalizedName = requireText(name, "O nome do produto é obrigatório.").trim();
        if (normalizedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "O nome do produto deve possuir no máximo " + MAX_NAME_LENGTH + " caracteres."
            );
        }
        return normalizedName;
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalizedDescription = description.trim();
        if (normalizedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "A descrição do produto deve possuir no máximo "
                            + MAX_DESCRIPTION_LENGTH + " caracteres."
            );
        }
        return normalizedDescription;
    }

    private static BigDecimal validateSalePrice(BigDecimal salePrice) {
        Objects.requireNonNull(salePrice, "O preço de venda é obrigatório.");
        if (salePrice.signum() <= 0) {
            throw new IllegalArgumentException("O preço de venda deve ser maior que zero.");
        }
        if (salePrice.scale() > 2) {
            throw new IllegalArgumentException(
                    "O preço de venda deve possuir no máximo duas casas decimais."
            );
        }
        return salePrice.setScale(2, RoundingMode.UNNECESSARY);
    }

    private static List<ProductIngredient> validateIngredients(
            List<ProductIngredient> ingredients
    ) {
        if (ingredients == null || ingredients.isEmpty()) {
            return List.of();
        }
        Set<UUID> ingredientIds = new HashSet<>();
        for (ProductIngredient ingredient : ingredients) {
            Objects.requireNonNull(ingredient, "O ingrediente do produto é obrigatório.");
            if (!ingredientIds.add(ingredient.ingredientId())) {
                throw new IllegalArgumentException(
                        "O mesmo ingrediente não pode ser informado mais de uma vez."
                );
            }
        }
        return List.copyOf(ingredients);
    }

    private static Instant validateUpdatedAt(Instant createdAt, Instant updatedAt) {
        Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException(
                    "A data de atualização não pode ser anterior à data de criação."
            );
        }
        return updatedAt;
    }

    private static String validateActor(String actor, String requiredMessage) {
        String normalizedActor = requireText(actor, requiredMessage).trim();
        if (normalizedActor.length() > MAX_ACTOR_LENGTH) {
            throw new IllegalArgumentException(
                    "O responsável deve possuir no máximo " + MAX_ACTOR_LENGTH + " caracteres."
            );
        }
        return normalizedActor;
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
                throw new IllegalArgumentException(
                        "Um produto ativo não pode possuir dados de desativação."
                );
            }
            return null;
        }

        Objects.requireNonNull(
                deactivatedAt,
                "A data de desativação é obrigatória para um produto inativo."
        );
        if (deactivatedAt.isBefore(createdAt) || deactivatedAt.isAfter(updatedAt)) {
            throw new IllegalArgumentException(
                    "A data de desativação deve estar entre a criação e a última atualização."
            );
        }
        return validateActor(
                deactivatedBy,
                "O responsável pela desativação é obrigatório para um produto inativo."
        );
    }

    private static long validateVersion(long version) {
        if (version < 0) {
            throw new IllegalArgumentException("A versão do produto não pode ser negativa.");
        }
        return version;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
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

    public UUID getCategoryId() {
        return categoryId;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public InventoryPolicy getInventoryPolicy() {
        return inventoryPolicy;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    public List<ProductIngredient> getIngredients() {
        return ingredients;
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
