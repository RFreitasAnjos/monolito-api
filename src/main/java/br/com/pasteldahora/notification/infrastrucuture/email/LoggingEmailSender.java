package br.com.pasteldahora.notification.infrastrucuture.email;

import br.com.pasteldahora.notification.application.port.out.EmailSender;
import br.com.pasteldahora.notification.application.port.out.MessageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailSender implements EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void send(MessageCommand messageCommand) {
        LOGGER.warn(
                "E-mail para {} com assunto '{}' não enviado: SMTP não configurado.",
                messageCommand.recipient(),
                messageCommand.subject()
        );
    }
}
