package br.com.pasteldahora.employee.application.port.in;

public record EmployeeSearchQuery(String term, Boolean active, int page, int size) {

    public EmployeeSearchQuery {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 100);
        term = term == null ? "" : term.trim();
    }
}
