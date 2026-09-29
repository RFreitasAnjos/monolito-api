package br.com.pasteldahora.employee.application.port.in;

import br.com.pasteldahora.employee.domain.model.EmployeeAccessRole;
import br.com.pasteldahora.employee.domain.model.EmployeePosition;

public record CreateEmployeeCommand(
        String name,
        String cpf,
        String email,
        String password,
        EmployeePosition position,
        EmployeeAccessRole accessRole,
        String actor
) {
}
