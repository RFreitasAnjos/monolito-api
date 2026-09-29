package br.com.pasteldahora.notification.application.port.out;

public record MessageCommand(
        String recipient,
        String subject,
        String textBody,
        String htmlBody
) {
}
