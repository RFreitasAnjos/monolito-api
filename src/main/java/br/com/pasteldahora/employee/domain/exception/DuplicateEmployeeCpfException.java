package br.com.pasteldahora.employee.domain.exception;

public class DuplicateEmployeeCpfException extends RuntimeException {

    public DuplicateEmployeeCpfException(String cpf) {
        super("Já existe um funcionário cadastrado com o CPF " + cpf + ".");
    }
}
