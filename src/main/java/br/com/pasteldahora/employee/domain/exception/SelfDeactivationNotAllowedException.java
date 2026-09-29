package br.com.pasteldahora.employee.domain.exception;

public class SelfDeactivationNotAllowedException extends RuntimeException {

    public SelfDeactivationNotAllowedException() {
        super("Não é permitido desativar a própria conta.");
    }
}
