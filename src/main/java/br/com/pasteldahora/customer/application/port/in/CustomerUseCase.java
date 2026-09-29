package br.com.pasteldahora.customer.application.port.in;

import br.com.pasteldahora.customer.domain.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerUseCase {

    Customer create(CreateCustomerCommand command);

    Customer update(UpdateCustomerCommand command);

    Customer findById(UUID id);

    List<Customer> findAll();

    void deactivate(UUID id, String actor);

    void reactivate(UUID id, String actor);
}
