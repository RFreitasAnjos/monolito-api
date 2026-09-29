package br.com.pasteldahora.customer.adapter.in.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyAccessCodeRequest(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "deve possuir exatamente 6 dígitos")
        String code
) {
}
