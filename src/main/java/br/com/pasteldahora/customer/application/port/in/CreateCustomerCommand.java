package br.com.pasteldahora.customer.application.port.in;

public record CreateCustomerCommand(
        String name,
        String email,
        String phone
) {
}
