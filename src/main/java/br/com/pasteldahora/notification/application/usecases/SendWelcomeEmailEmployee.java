package br.com.pasteldahora.notification.application.usecases;

import br.com.pasteldahora.notification.application.port.out.EmailTemplateRenderer;
import br.com.pasteldahora.notification.application.port.out.MessageCommand;

import java.util.Map;

public class SendWelcomeEmailEmployee {

    private final EmailTemplateRenderer templateRenderer;

    public SendWelcomeEmailEmployee(EmailTemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public MessageCommand createMessage(String recipient, String name) {
        String normalizedName = requireText(name, "O nome do funcionário é obrigatório.");
        String subject = "Bem-vindo à equipe Pastel da Hora";

        String textBody = """
                Olá, %s!

                Seu cadastro de funcionário foi realizado com sucesso.

                Seja bem-vindo à equipe Pastel da Hora!

                Atenciosamente,
                Equipe Pastel da Hora
                """.formatted(normalizedName);

        return new MessageCommand(
                requireText(recipient, "O destinatário é obrigatório."),
                subject,
                textBody,
                templateRenderer.render(
                        "email/welcome-employee",
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
