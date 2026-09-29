package br.com.pasteldahora.catalog.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTests {

    private static final UUID CATEGORY_ID = UUID.fromString(
            "f616ec6c-25f6-4c4b-9f74-741bc33427cf"
    );
    private static final Instant CREATED_AT = Instant.parse("2026-09-29T12:00:00Z");
    private static final String ACTOR = "admin@pasteldahora.local";

    @Test
    void shouldCreateProductWithCommercialDataAndAudit() {
        Product product = createProduct();

        assertEquals("PASTEL-CARNE", product.getSku());
        assertEquals("Pastel de carne", product.getName());
        assertEquals("Pastel tradicional", product.getDescription());
        assertEquals(CATEGORY_ID, product.getCategoryId());
        assertEquals(new BigDecimal("12.50"), product.getSalePrice());
        assertEquals(InventoryPolicy.RECIPE_BASED, product.getInventoryPolicy());
        assertTrue(product.isActive());
        assertEquals(CREATED_AT, product.getCreatedAt());
        assertEquals(CREATED_AT, product.getUpdatedAt());
        assertEquals(ACTOR, product.getCreatedBy());
        assertEquals(ACTOR, product.getUpdatedBy());
        assertNull(product.getDeactivatedAt());
        assertNull(product.getDeactivatedBy());
        assertEquals(0, product.getVersion());
    }

    @Test
    void shouldUpdateCommercialDataWithoutChangingIdentityStatusOrCreationAudit() {
        Product product = createProduct();
        Instant updatedAt = CREATED_AT.plusSeconds(60);
        UUID newCategoryId = UUID.randomUUID();

        Product updated = product.update(
                "pastel-carne-queijo",
                "Pastel de carne com queijo",
                "Receita atualizada",
                newCategoryId,
                new BigDecimal("15.00"),
                InventoryPolicy.RECIPE_BASED,
                true,
                List.of(),
                "manager@pasteldahora.local",
                updatedAt
        );

        assertEquals(product.getId(), updated.getId());
        assertEquals("PASTEL-CARNE-QUEIJO", updated.getSku());
        assertEquals("Pastel de carne com queijo", updated.getName());
        assertEquals(newCategoryId, updated.getCategoryId());
        assertEquals(new BigDecimal("15.00"), updated.getSalePrice());
        assertTrue(updated.isCustomizable());
        assertTrue(updated.getIngredients().isEmpty());
        assertTrue(updated.isActive());
        assertEquals(CREATED_AT, updated.getCreatedAt());
        assertEquals(ACTOR, updated.getCreatedBy());
        assertEquals(updatedAt, updated.getUpdatedAt());
        assertEquals("manager@pasteldahora.local", updated.getUpdatedBy());
        assertEquals(product.getVersion(), updated.getVersion());
    }

    @Test
    void shouldManageProductLifecycleAndDeactivationAuditIdempotently() {
        Product product = createProduct();
        Instant deactivatedAt = CREATED_AT.plusSeconds(60);
        Instant reactivatedAt = CREATED_AT.plusSeconds(120);

        Product deactivated = product.deactivate(ACTOR, deactivatedAt);
        Product reactivated = deactivated.reactivate(ACTOR, reactivatedAt);

        assertFalse(deactivated.isActive());
        assertEquals(deactivatedAt, deactivated.getDeactivatedAt());
        assertEquals(ACTOR, deactivated.getDeactivatedBy());
        assertSame(deactivated, deactivated.deactivate(ACTOR, deactivatedAt.plusSeconds(1)));

        assertTrue(reactivated.isActive());
        assertNull(reactivated.getDeactivatedAt());
        assertNull(reactivated.getDeactivatedBy());
        assertEquals(reactivatedAt, reactivated.getUpdatedAt());
        assertSame(reactivated, reactivated.reactivate(ACTOR, reactivatedAt.plusSeconds(1)));
    }

    @Test
    void shouldRestorePersistedProduct() {
        UUID id = UUID.randomUUID();
        Instant updatedAt = CREATED_AT.plusSeconds(60);

        Product product = Product.restore(
                id,
                "REFRI-LATA",
                "Refrigerante em lata",
                null,
                CATEGORY_ID,
                new BigDecimal("6.5"),
                InventoryPolicy.DIRECT_STOCK,
                false,
                List.of(),
                false,
                CREATED_AT,
                updatedAt,
                ACTOR,
                ACTOR,
                updatedAt,
                ACTOR,
                3
        );

        assertEquals(id, product.getId());
        assertEquals("REFRI-LATA", product.getSku());
        assertEquals(new BigDecimal("6.50"), product.getSalePrice());
        assertEquals(InventoryPolicy.DIRECT_STOCK, product.getInventoryPolicy());
        assertFalse(product.isActive());
        assertEquals(3, product.getVersion());
    }

    @Test
    void shouldNormalizeBlankDescriptionToNull() {
        Product product = Product.create(
                "AGUA-500",
                "Água",
                "   ",
                CATEGORY_ID,
                new BigDecimal("4.00"),
                InventoryPolicy.DIRECT_STOCK,
                false,
                List.of(),
                ACTOR,
                CREATED_AT
        );

        assertNull(product.getDescription());
    }

    @Test
    void shouldRejectInvalidSkuNameCategoryAndPrice() {
        assertThrows(IllegalArgumentException.class, () -> createProduct("", "Pastel", "12.50"));
        assertThrows(IllegalArgumentException.class, () -> createProduct("SKU inválido", "Pastel", "12.50"));
        assertThrows(IllegalArgumentException.class, () -> createProduct("SKU", " ", "12.50"));
        assertThrows(IllegalArgumentException.class, () -> createProduct("SKU", "A".repeat(121), "12.50"));
        assertThrows(NullPointerException.class, () -> Product.create(
                "SKU",
                "Pastel",
                null,
                null,
                new BigDecimal("12.50"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                List.of(),
                ACTOR,
                CREATED_AT
        ));
        assertThrows(IllegalArgumentException.class, () -> createProduct("SKU", "Pastel", "0"));
        assertThrows(IllegalArgumentException.class, () -> createProduct("SKU", "Pastel", "-1"));
        assertThrows(IllegalArgumentException.class, () -> createProduct("SKU", "Pastel", "12.345"));
    }

    @Test
    void shouldRejectInconsistentAuditAndVersionData() {
        assertThrows(IllegalArgumentException.class, () -> Product.restore(
                UUID.randomUUID(),
                "SKU",
                "Pastel",
                null,
                CATEGORY_ID,
                new BigDecimal("12.50"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                List.of(),
                true,
                CREATED_AT,
                CREATED_AT,
                ACTOR,
                ACTOR,
                CREATED_AT,
                ACTOR,
                0
        ));

        assertThrows(NullPointerException.class, () -> Product.restore(
                UUID.randomUUID(),
                "SKU",
                "Pastel",
                null,
                CATEGORY_ID,
                new BigDecimal("12.50"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                List.of(),
                false,
                CREATED_AT,
                CREATED_AT,
                ACTOR,
                ACTOR,
                null,
                null,
                0
        ));

        assertThrows(IllegalArgumentException.class, () -> Product.restore(
                UUID.randomUUID(),
                "SKU",
                "Pastel",
                null,
                CATEGORY_ID,
                new BigDecimal("12.50"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                List.of(),
                true,
                CREATED_AT,
                CREATED_AT,
                ACTOR,
                ACTOR,
                null,
                null,
                -1
        ));

        assertThrows(IllegalArgumentException.class, () -> Product.restore(
                UUID.randomUUID(),
                "SKU",
                "Pastel",
                null,
                CATEGORY_ID,
                new BigDecimal("12.50"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                List.of(),
                false,
                CREATED_AT,
                CREATED_AT.plusSeconds(60),
                ACTOR,
                ACTOR,
                CREATED_AT.plusSeconds(120),
                ACTOR,
                0
        ));
    }

    private static Product createProduct() {
        return createProduct("  pastel-carne  ", "  Pastel de carne  ", "12.50");
    }

    private static Product createProduct(String sku, String name, String price) {
        return Product.create(
                sku,
                name,
                "  Pastel tradicional  ",
                CATEGORY_ID,
                new BigDecimal(price),
                InventoryPolicy.RECIPE_BASED,
                false,
                List.of(),
                ACTOR,
                CREATED_AT
        );
    }
}
