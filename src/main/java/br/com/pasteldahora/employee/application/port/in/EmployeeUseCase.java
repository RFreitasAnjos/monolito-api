package br.com.pasteldahora.employee.application.port.in;

import br.com.pasteldahora.employee.domain.model.Employee;

import java.util.UUID;

/**
 * Porta de entrada: operações que os adaptadores podem solicitar à aplicação.
 */
public interface EmployeeUseCase {

    Employee create(CreateEmployeeCommand command);

    Employee update(UpdateEmployeeCommand command);

    PageResult<Employee> search(EmployeeSearchQuery query);

    Employee findById(UUID id);

    void deactivate(UUID id, String actor);

    void reactivate(UUID id, String actor);
}
