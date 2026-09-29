package br.com.pasteldahora.customer.application.port.out;

import br.com.pasteldahora.customer.domain.model.Customer;
import br.com.pasteldahora.customer.domain.model.CustomerAccessChannel;

import java.time.Instant;

public interface CustomerAccessCodeSenderPort {

    void send(
            Customer customer,
            CustomerAccessChannel channel,
            String code,
            Instant expiresAt
    );
}
