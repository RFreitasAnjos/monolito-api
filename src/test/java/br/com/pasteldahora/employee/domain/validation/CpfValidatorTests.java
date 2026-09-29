package br.com.pasteldahora.employee.domain.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpfValidatorTests {

    @Test
    void shouldNormalizeAndValidateCpf() {
        assertEquals("52998224725", CpfValidator.normalizeAndValidate("529.982.247-25"));
    }

    @Test
    void shouldRejectInvalidCpf() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CpfValidator.normalizeAndValidate("111.111.111-11")
        );
    }
}
