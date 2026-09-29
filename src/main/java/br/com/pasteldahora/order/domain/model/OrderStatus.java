package br.com.pasteldahora.order.domain.model;

public enum OrderStatus {

    OPEN("Aberto"),
    PREPARING("Preparando"),
    DELIVERY("A caminho"),
    COMPLETED("Concluído"),
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
