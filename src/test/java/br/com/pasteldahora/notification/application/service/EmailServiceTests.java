package br.com.pasteldahora.notification.application.service;

import br.com.pasteldahora.notification.application.port.out.EmailSender;
import br.com.pasteldahora.notification.application.port.out.EmailTemplateRenderer;
import br.com.pasteldahora.notification.application.port.out.MessageCommand;
import br.com.pasteldahora.notification.application.usecases.SendEmailLoginCustomerRequest;
import br.com.pasteldahora.notification.application.usecases.SendWelcomeEmailCustomer;
import br.com.pasteldahora.notification.application.usecases.SendWelcomeEmailEmployee;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailServiceTests {

    private static final Instant EXPIRES_AT = Instant.parse("2026-09-29T18:50:00Z");
    private static final EmailTemplateRenderer TEMPLATE_RENDERER =
            (template, variables) -> "<html><body>" + template + " " + variables + "</body></html>";

    @Test
    void shouldBuildMessagesInIndependentUseCases() {
        MessageCommand employeeMessage = new SendWelcomeEmailEmployee(TEMPLATE_RENDERER)
                .createMessage("employee@example.com", "Ana");
        MessageCommand customerMessage = new SendWelcomeEmailCustomer(TEMPLATE_RENDERER)
                .createMessage("customer@example.com", "Carlos");
        MessageCommand loginMessage = new SendEmailLoginCustomerRequest(
                TEMPLATE_RENDERER,
                ZoneId.of("America/Sao_Paulo")
        ).createMessage("customer@example.com", "Carlos", "123456", EXPIRES_AT);

        assertEquals("employee@example.com", employeeMessage.recipient());
        assertTrue(employeeMessage.textBody().contains("cadastro de funcionário"));
        assertTrue(customerMessage.textBody().contains("código temporário"));
        assertTrue(loginMessage.textBody().contains("29/09/2026 às 15:50"));
        assertTrue(loginMessage.htmlBody().contains("123456"));
    }

    @Test
    void shouldDelegateEachOperationToEmailSender() {
        CapturingEmailSender sender = new CapturingEmailSender();
        EmailUseCases emailUseCases = new EmailService(
                sender,
                new SendWelcomeEmailEmployee(TEMPLATE_RENDERER),
                new SendWelcomeEmailCustomer(TEMPLATE_RENDERER),
                new SendEmailLoginCustomerRequest(
                        TEMPLATE_RENDERER,
                        ZoneId.of("America/Sao_Paulo")
                )
        );

        emailUseCases.sendWelcomeEmailEmployee("employee@example.com", "Ana");
        emailUseCases.sendWelcomeEmailCustomer("customer@example.com", "Carlos");
        emailUseCases.sendCustomerLoginCode(
                "customer@example.com",
                "Carlos",
                "654321",
                EXPIRES_AT
        );

        assertEquals(3, sender.messages.size());
        assertTrue(sender.messages.get(0).subject().contains("equipe"));
        assertTrue(sender.messages.get(1).subject().contains("Pastel da Hora"));
        assertTrue(sender.messages.get(2).textBody().contains("654321"));
        assertTrue(sender.messages.get(2).htmlBody().contains("customer-login-code"));
    }

    @Test
    void shouldRejectInvalidCustomerLoginCode() {
        SendEmailLoginCustomerRequest useCase = new SendEmailLoginCustomerRequest(
                TEMPLATE_RENDERER,
                ZoneId.of("America/Sao_Paulo")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.createMessage(
                        "customer@example.com",
                        "Carlos",
                        "123",
                        EXPIRES_AT
                )
        );
    }

    private static final class CapturingEmailSender implements EmailSender {

        private final List<MessageCommand> messages = new ArrayList<>();

        @Override
        public void send(MessageCommand messageCommand) {
            messages.add(messageCommand);
        }
    }
}
