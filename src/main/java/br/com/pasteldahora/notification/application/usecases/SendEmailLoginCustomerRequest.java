package br.com.pasteldahora.notification.application.usecases;

import br.com.pasteldahora.notification.application.port.out.EmailTemplateRenderer;
import br.com.pasteldahora.notification.application.port.out.MessageCommand;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

public class SendEmailLoginCustomerRequest {

    private static final DateTimeFormatter EXPIRATION_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final EmailTemplateRenderer templateRenderer;
    private final ZoneId zoneId;

    public SendEmailLoginCustomerRequest(
            EmailTemplateRenderer templateRenderer,
            ZoneId zoneId
    ) {
        this.templateRenderer = templateRenderer;
        this.zoneId = zoneId;
    }

    public MessageCommand createMessage(
            String recipient,
            String name,
            String code,
            Instant expiresAt
    ) {
        String normalizedName = requireText(name, "O nome do cliente é obrigatório.");
        String normalizedCode = requireText(code, "O código de acesso é obrigatório.");
        if (!normalizedCode.matches("\\d{6}")) {
            throw new IllegalArgumentException(
                    "O código de acesso deve possuir exatamente seis dígitos."
            );
        }
        String formattedExpiration = EXPIRATION_FORMATTER
                .withZone(zoneId)
                .format(Objects.requireNonNull(
                        expiresAt,
                        "A expiração do código é obrigatória."
                ));

        String textBody = """
                Olá, %s!

                Seu código de acesso é: %s

                O código é válido até %s e só pode ser utilizado uma vez.
                Não compartilhe este código. A Pastel da Hora nunca solicitará
                esse código por telefone, e-mail ou WhatsApp.

                Se você não solicitou este código, ignore esta mensagem.
                """.formatted(
                normalizedName,
                normalizedCode,
                formattedExpiration
        );

        return new MessageCommand(
                requireText(recipient, "O destinatário é obrigatório."),
                "Código de acesso Pastel da Hora",
                textBody,
                templateRenderer.render(
                        "email/customer-login-code",
                        Map.of(
                                "name", normalizedName,
                                "code", normalizedCode,
                                "expiresAt", formattedExpiration
                        )
                )
        );
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
