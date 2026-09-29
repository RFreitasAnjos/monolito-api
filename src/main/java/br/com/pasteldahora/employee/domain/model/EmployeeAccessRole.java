package br.com.pasteldahora.employee.domain.model;

public enum EmployeeAccessRole {
    ADMIN("Administrador"),
    MANAGER("Gerente"),
    OPERATOR("Operador");

    private final String description;

    EmployeeAccessRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
