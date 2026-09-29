package br.com.pasteldahora.customer.application.port.in;

public record VerifyCustomerAccessCodeCommand(
        String email,
        String code
) {
}
