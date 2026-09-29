package br.com.pasteldahora.employee.domain.exception;

public class DuplicateEmployeeEmailException extends RuntimeException {

    public DuplicateEmployeeEmailException(String email) {
        super("Já existe um funcionário cadastrado com o e-mail " + email + ".");
    }
}
