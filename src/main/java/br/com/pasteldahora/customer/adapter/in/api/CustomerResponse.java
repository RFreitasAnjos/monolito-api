package br.com.pasteldahora.customer.adapter.in.api;

import br.com.pasteldahora.customer.domain.model.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String phone,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.getVersion()
        );
    }
}
