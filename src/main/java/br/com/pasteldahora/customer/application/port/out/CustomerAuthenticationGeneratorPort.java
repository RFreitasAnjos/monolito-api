package br.com.pasteldahora.customer.application.port.out;

public interface CustomerAuthenticationGeneratorPort {

    String generateCode();

    String generateToken();
}
