package br.com.pasteldahora.catalog.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTests {

    @Test
    void shouldManageCategoryLifecycleAndAudit() {
        Instant createdAt = Instant.parse("2026-09-29T12:00:00Z");
        Category category = Category.create(
                "  Bebidas  ",
                "admin@pasteldahora.local",
                createdAt
        );

        Category updated = category.update(
                "Bebidas geladas",
                "manager@pasteldahora.local",
                createdAt.plusSeconds(60)
        );
        Category deactivated = updated.deactivate(
                "manager@pasteldahora.local",
                createdAt.plusSeconds(120)
        );
        Category reactivated = deactivated.reactivate(
                "manager@pasteldahora.local",
                createdAt.plusSeconds(180)
        );

        assertEquals("Bebidas", category.getName());
        assertEquals("Bebidas geladas", updated.getName());
        assertEquals(category.getId(), updated.getId());
        assertFalse(deactivated.isActive());
        assertEquals(createdAt.plusSeconds(120), deactivated.getDeactivatedAt());
        assertTrue(reactivated.isActive());
        assertNull(reactivated.getDeactivatedAt());
        assertSame(reactivated, reactivated.reactivate(
                "manager@pasteldahora.local",
                createdAt.plusSeconds(240)
        ));
    }
}
