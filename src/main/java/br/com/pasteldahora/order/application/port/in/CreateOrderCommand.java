package br.com.pasteldahora.order.application.port.in;

import java.util.UUID;

public record CreateOrderCommand(UUID customerId, String actor) {
}
