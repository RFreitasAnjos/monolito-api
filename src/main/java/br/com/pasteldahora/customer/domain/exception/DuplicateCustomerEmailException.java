package br.com.pasteldahora.customer.domain.exception;

public class DuplicateCustomerEmailException extends RuntimeException {

    public DuplicateCustomerEmailException(String email) {
        super("Já existe um cliente cadastrado com o e-mail " + email + ".");
    }
}
