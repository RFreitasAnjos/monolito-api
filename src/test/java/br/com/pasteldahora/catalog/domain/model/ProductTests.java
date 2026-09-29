package br.com.pasteldahora.catalog.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTests {

    @Test
    void shouldCreateAndUpdateProductWithoutChangingIdentityOrStatus() {
        Product product = Product.create("  Pastel de carne  ", "  Pastel tradicional  ");

        Product updated = product.update("Pastel de carne com queijo", "Receita atualizada");

        assertEquals(product.getId(), updated.getId());
        assertEquals("Pastel de carne", product.getName());
        assertEquals("Pastel tradicional", product.getDescription());
        assertEquals("Pastel de carne com queijo", updated.getName());
        assertEquals("Receita atualizada", updated.getDescription());
        assertTrue(updated.isActive());
    }

    @Test
    void shouldManageProductLifecycleIdempotently() {
        Product product = Product.create("Caldo de cana", null);

        Product deactivated = product.deactivate();
        Product reactivated = deactivated.reactivate();

        assertFalse(deactivated.isActive());
        assertTrue(reactivated.isActive());
        assertSame(deactivated, deactivated.deactivate());
        assertSame(reactivated, reactivated.reactivate());
    }

    @Test
    void shouldRestorePersistedProduct() {
        UUID id = UUID.randomUUID();

        Product product = Product.restore(id, "Pastel de queijo", "Sem carne", false);

        assertEquals(id, product.getId());
        assertEquals("Pastel de queijo", product.getName());
        assertEquals("Sem carne", product.getDescription());
        assertFalse(product.isActive());
    }

    @Test
    void shouldNormalizeBlankDescriptionToNull() {
        Product product = Product.create("Água", "   ");

        assertNull(product.getDescription());
    }

    @Test
    void shouldRejectInvalidProductData() {
        assertThrows(IllegalArgumentException.class, () -> Product.create(" ", "Descrição"));
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create("A".repeat(121), "Descrição")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create("Pastel", "A".repeat(501))
        );
        assertThrows(
                NullPointerException.class,
                () -> Product.restore(null, "Pastel", "Descrição", true)
        );
    }
}
