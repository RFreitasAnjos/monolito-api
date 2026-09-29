package br.com.pasteldahora.notification.application.usecases;

import br.com.pasteldahora.notification.application.port.out.EmailTemplateRenderer;
import br.com.pasteldahora.notification.application.port.out.MessageCommand;

import java.util.Map;

public class SendWelcomeEmailCustomer {

    private final EmailTemplateRenderer templateRenderer;

    public SendWelcomeEmailCustomer(EmailTemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public MessageCommand createMessage(String recipient, String name) {
        String normalizedName = requireText(name, "O nome do cliente é obrigatório.");
        String subject = "Bem-vindo à Pastel da Hora";

        String textBody = """
                Olá, %s!

                Seu cadastro foi realizado com sucesso.

                Agora você pode acessar sua conta usando um código temporário.

                Atenciosamente,
                Equipe Pastel da Hora
                """.formatted(normalizedName);

        return new MessageCommand(
                requireText(recipient, "O destinatário é obrigatório."),
                subject,
                textBody,
                templateRenderer.render(
                        "email/welcome-customer",
                        Map.of("name", normalizedName)
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
