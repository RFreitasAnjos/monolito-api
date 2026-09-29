package br.com.pasteldahora.customer.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerTests {

    private static final Instant NOW = Instant.parse("2026-09-29T15:00:00Z");

    @Test
    void shouldNormalizeCustomerDataAndManageLifecycle() {
        Customer customer = Customer.create(
                "  Maria da Silva  ",
                "  MARIA@EXAMPLE.COM ",
                "(11) 99999-9999",
                "customer-api",
                NOW
        );

        assertEquals("Maria da Silva", customer.getName());
        assertEquals("maria@example.com", customer.getEmail());
        assertEquals("11999999999", customer.getPhone());
        assertTrue(customer.isActive());

        Customer deactivated = customer.deactivate("admin@example.com", NOW.plusSeconds(60));
        assertFalse(deactivated.isActive());
        assertEquals("admin@example.com", deactivated.getDeactivatedBy());

        Customer reactivated = deactivated.reactivate(
                "admin@example.com",
                NOW.plusSeconds(120)
        );
        assertTrue(reactivated.isActive());
    }

    @Test
    void shouldUpdateOnlySimpleRegistrationData() {
        Customer customer = Customer.create(
                "Maria",
                "maria@example.com",
                "11999999999",
                "customer-api",
                NOW
        );

        Customer updated = customer.update(
                "Maria Souza",
                "maria.souza@example.com",
                "11988888888",
                "manager@example.com",
                NOW.plusSeconds(60)
        );

        assertEquals("Maria Souza", updated.getName());
        assertEquals("maria.souza@example.com", updated.getEmail());
        assertEquals("11988888888", updated.getPhone());
    }

    @Test
    void shouldRejectInvalidCustomerData() {
        assertThrows(IllegalArgumentException.class, () -> Customer.create(
                "Maria",
                "invalid-email",
                "11999999999",
                "customer-api",
                NOW
        ));
        assertThrows(IllegalArgumentException.class, () -> Customer.create(
                "Maria",
                "maria@example.com",
                "1",
                "customer-api",
                NOW
        ));
    }
}
