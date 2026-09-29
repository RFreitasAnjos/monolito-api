package br.com.pasteldahora.customer.application.service;

import br.com.pasteldahora.customer.application.port.in.CreateCustomerCommand;
import br.com.pasteldahora.customer.application.port.in.CustomerUseCase;
import br.com.pasteldahora.customer.application.port.in.UpdateCustomerCommand;
import br.com.pasteldahora.customer.application.port.out.CustomerRepositoryPort;
import br.com.pasteldahora.customer.domain.exception.CustomerNotFoundException;
import br.com.pasteldahora.customer.domain.exception.DuplicateCustomerEmailException;
import br.com.pasteldahora.customer.domain.model.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CustomerApplicationService implements CustomerUseCase {

    private static final String SELF_REGISTRATION_ACTOR = "customer-api";

    private final CustomerRepositoryPort repository;
    private final Clock clock;

    public CustomerApplicationService(
            CustomerRepositoryPort repository,
            Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Customer create(CreateCustomerCommand command) {
        Customer customer = Customer.create(
                command.name(),
                command.email(),
                command.phone(),
                SELF_REGISTRATION_ACTOR,
                Instant.now(clock)
        );
        if (repository.existsByEmail(customer.getEmail())) {
            throw new DuplicateCustomerEmailException(customer.getEmail());
        }
        return repository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(UpdateCustomerCommand command) {
        Customer customer = findById(command.id());
        Customer updated = customer.update(
                command.name(),
                command.email(),
                command.phone(),
                command.actor(),
                Instant.now(clock)
        );
        if (repository.existsByEmailAndIdNot(updated.getEmail(), updated.getId())) {
            throw new DuplicateCustomerEmailException(updated.getEmail());
        }
        return repository.save(updated);
    }

    @Override
    public Customer findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deactivate(UUID id, String actor) {
        repository.save(findById(id).deactivate(actor, Instant.now(clock)));
    }

    @Override
    @Transactional
    public void reactivate(UUID id, String actor) {
        repository.save(findById(id).reactivate(actor, Instant.now(clock)));
    }

}
