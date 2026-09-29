package br.com.pasteldahora.customer.domain.exception;

public class InvalidCustomerAccessCodeException extends RuntimeException {

    public InvalidCustomerAccessCodeException() {
        super("O código informado é inválido ou expirou.");
    }
}
