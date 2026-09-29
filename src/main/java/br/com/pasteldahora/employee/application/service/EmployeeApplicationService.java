package br.com.pasteldahora.employee.application.service;

import br.com.pasteldahora.employee.application.port.in.CreateEmployeeCommand;
import br.com.pasteldahora.employee.application.port.in.EmployeeSearchQuery;
import br.com.pasteldahora.employee.application.port.in.EmployeeUseCase;
import br.com.pasteldahora.employee.application.port.in.PageResult;
import br.com.pasteldahora.employee.application.port.in.UpdateEmployeeCommand;
import br.com.pasteldahora.employee.application.port.out.EmployeeRepositoryPort;
import br.com.pasteldahora.employee.application.port.out.PasswordHashPort;
import br.com.pasteldahora.employee.domain.exception.DuplicateEmployeeCpfException;
import br.com.pasteldahora.employee.domain.exception.DuplicateEmployeeEmailException;
import br.com.pasteldahora.employee.domain.exception.EmployeeNotFoundException;
import br.com.pasteldahora.employee.domain.exception.SelfDeactivationNotAllowedException;
import br.com.pasteldahora.employee.domain.model.Employee;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Casos de uso compartilhados pelos adaptadores MVC e REST.
 */
public final class EmployeeApplicationService implements EmployeeUseCase {

    private final EmployeeRepositoryPort repository;
    private final PasswordHashPort passwordHashPort;
    private final Clock clock;

    public EmployeeApplicationService(
            EmployeeRepositoryPort repository,
            PasswordHashPort passwordHashPort,
            Clock clock
    ) {
        this.repository = repository;
        this.passwordHashPort = passwordHashPort;
        this.clock = clock;
    }

    @Override
    public Employee create(CreateEmployeeCommand command) {
        Employee employee = Employee.create(
                command.name(),
                command.cpf(),
                command.email(),
                passwordHashPort.hash(validatePassword(command.password())),
                command.position(),
                command.accessRole(),
                command.actor(),
                Instant.now(clock)
        );
        if (repository.existsByCpf(employee.getCpf())) {
            throw new DuplicateEmployeeCpfException(employee.getCpf());
        }
        if (repository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmployeeEmailException(employee.getEmail());
        }
        return repository.save(employee);
    }

    @Override
    public Employee update(UpdateEmployeeCommand command) {
        Employee employee = findById(command.id());
        String newPasswordHash = command.password() == null || command.password().isBlank()
                ? null
                : passwordHashPort.hash(validatePassword(command.password()));

        Employee updatedEmployee = employee.update(
                command.name(),
                command.email(),
                command.position(),
                command.accessRole(),
                newPasswordHash,
                command.actor(),
                Instant.now(clock)
        );
        if (repository.existsByEmailAndIdNot(updatedEmployee.getEmail(), command.id())) {
            throw new DuplicateEmployeeEmailException(updatedEmployee.getEmail());
        }
        return repository.save(updatedEmployee);
    }

    @Override
    public PageResult<Employee> search(EmployeeSearchQuery query) {
        return repository.search(query);
    }

    @Override
    public Employee findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    public void deactivate(UUID id, String actor) {
        Employee employee = findById(id);
        if (employee.getEmail().equalsIgnoreCase(actor)) {
            throw new SelfDeactivationNotAllowedException();
        }
        repository.save(employee.deactivate(actor, Instant.now(clock)));
    }

    @Override
    public void reactivate(UUID id, String actor) {
        repository.save(findById(id).reactivate(actor, Instant.now(clock)));
    }

    private static String validatePassword(String password) {
        if (password == null || password.length() < 10) {
            throw new IllegalArgumentException("A senha deve possuir pelo menos 10 caracteres.");
        }
        return password;
    }
}
