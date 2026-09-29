package br.com.pasteldahora.customer.adapter.in.api;

import br.com.pasteldahora.customer.domain.model.CustomerAccessChannel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestAccessCodeRequest(
        @NotBlank @Email @Size(max = 160) String email,
        @NotNull CustomerAccessChannel channel
) {
}
