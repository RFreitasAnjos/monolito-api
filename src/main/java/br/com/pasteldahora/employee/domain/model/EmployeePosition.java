package br.com.pasteldahora.employee.domain.model;

public enum EmployeePosition {
    MANAGER("Gerente"),
    ATTENDANT("Atendente"),
    COOK("Cozinheiro"),
    DELIVERY_DRIVER("Entregador");

    private final String description;

    EmployeePosition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
