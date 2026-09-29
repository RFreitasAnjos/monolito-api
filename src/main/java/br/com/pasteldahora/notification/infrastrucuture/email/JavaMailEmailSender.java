package br.com.pasteldahora.notification.infrastrucuture.email;

import br.com.pasteldahora.notification.application.port.out.EmailSender;
import br.com.pasteldahora.notification.application.port.out.MessageCommand;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;

public class JavaMailEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    public JavaMailEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(MessageCommand messageCommand) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(messageCommand.recipient());
            helper.setSubject(messageCommand.subject());
            helper.setText(messageCommand.textBody(), messageCommand.htmlBody());
        });
    }
}