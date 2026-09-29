package br.com.pasteldahora.customer.application.port.in;

import br.com.pasteldahora.customer.domain.model.CustomerAccessChannel;

public record RequestCustomerAccessCodeCommand(
        String email,
        CustomerAccessChannel channel
) {
}
