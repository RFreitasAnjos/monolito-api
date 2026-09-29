package br.com.pasteldahora.employee.application.port.in;

import br.com.pasteldahora.employee.domain.model.EmployeeAccessRole;
import br.com.pasteldahora.employee.domain.model.EmployeePosition;

import java.util.UUID;

public record UpdateEmployeeCommand(
        UUID id,
        String name,
        String email,
        EmployeePosition position,
        EmployeeAccessRole accessRole,
        String password,
        String actor
) {
}
