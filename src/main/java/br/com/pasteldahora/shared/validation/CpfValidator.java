package br.com.pasteldahora.shared.validation;

public final class CpfValidator {

    private CpfValidator() {
    }

    public static String normalizeAndValidate(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF é obrigatório.");
        }

        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            throw new IllegalArgumentException("O CPF deve possuir 11 dígitos.");
        }
        if (digits.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("O CPF informado é inválido.");
        }

        int firstDigit = calculateDigit(digits.substring(0, 9));
        int secondDigit = calculateDigit(digits.substring(0, 10));
        if (firstDigit != Character.getNumericValue(digits.charAt(9))
                || secondDigit != Character.getNumericValue(digits.charAt(10))) {
            throw new IllegalArgumentException("O CPF informado é inválido.");
        }
        return digits;
    }

    private static int calculateDigit(String digits) {
        int weight = digits.length() + 1;
        int sum = 0;
        for (int index = 0; index < digits.length(); index++) {
            sum += Character.getNumericValue(digits.charAt(index)) * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
