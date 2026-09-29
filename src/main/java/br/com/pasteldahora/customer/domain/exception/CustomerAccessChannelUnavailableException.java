package br.com.pasteldahora.customer.domain.exception;

public class CustomerAccessChannelUnavailableException extends RuntimeException {

    public CustomerAccessChannelUnavailableException(String channel) {
        super("O canal " + channel + " não está configurado no momento.");
    }

    public CustomerAccessChannelUnavailableException(String channel, Throwable cause) {
        super("O canal " + channel + " está temporariamente indisponível.", cause);
    }
}
