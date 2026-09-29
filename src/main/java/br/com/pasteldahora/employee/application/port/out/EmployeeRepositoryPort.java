package br.com.pasteldahora.employee.application.port.out;

import br.com.pasteldahora.employee.application.port.in.EmployeeSearchQuery;
import br.com.pasteldahora.employee.application.port.in.PageResult;
import br.com.pasteldahora.employee.domain.model.Employee;

import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída: contrato que isola o caso de uso da tecnologia de persistência.
 */
public interface EmployeeRepositoryPort {

    Employee save(Employee employee);

    PageResult<Employee> search(EmployeeSearchQuery query);

    Optional<Employee> findById(UUID id);

    Optional<Employee> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
