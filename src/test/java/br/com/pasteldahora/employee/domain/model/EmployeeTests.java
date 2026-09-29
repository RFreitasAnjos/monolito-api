package br.com.pasteldahora.employee.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTests {

    @Test
    void shouldManageEmployeeLifecycleAndAudit() {
        Instant createdAt = Instant.parse("2026-09-28T12:00:00Z");
        Employee employee = Employee.create(
                "Maria da Silva",
                "529.982.247-25",
                "maria@pasteldahora.com",
                "bcrypt-hash",
                EmployeePosition.ATTENDANT,
                EmployeeAccessRole.OPERATOR,
                "admin@pasteldahora.com",
                createdAt
        );

        Employee deactivated = employee.deactivate(
                "admin@pasteldahora.com",
                createdAt.plusSeconds(60)
        );
        Employee reactivated = deactivated.reactivate(
                "admin@pasteldahora.com",
                createdAt.plusSeconds(120)
        );

        assertTrue(employee.isActive());
        assertFalse(deactivated.isActive());
        assertEquals(createdAt.plusSeconds(60), deactivated.getDeactivatedAt());
        assertTrue(reactivated.isActive());
        assertEquals(null, reactivated.getDeactivatedAt());
    }
}
