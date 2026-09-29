package br.com.pasteldahora.customer.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID id) {
        super("Cliente não encontrado: " + id);
    }
}
