package br.com.pasteldahora.customer.application.port.in;

import java.util.UUID;

public record UpdateCustomerCommand(
        UUID id,
        String name,
        String email,
        String phone,
        String actor
) {
}
