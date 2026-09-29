package br.com.pasteldahora.employee.domain.validation;

/**
 * Regra de domínio para normalização e validação de CPF.
 */
public final class CpfValidator {

    private CpfValidator() {
    }

    public static String normalizeAndValidate(String cpf) {
        return br.com.pasteldahora.shared.validation.CpfValidator.normalizeAndValidate(cpf);
    }
}
