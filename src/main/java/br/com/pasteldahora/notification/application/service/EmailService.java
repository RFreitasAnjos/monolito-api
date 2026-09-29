package br.com.pasteldahora.notification.application.service;

import br.com.pasteldahora.notification.application.port.out.EmailSender;
import br.com.pasteldahora.notification.application.usecases.SendEmailLoginCustomerRequest;
import br.com.pasteldahora.notification.application.usecases.SendWelcomeEmailCustomer;
import br.com.pasteldahora.notification.application.usecases.SendWelcomeEmailEmployee;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EmailService implements EmailUseCases {

    private final EmailSender emailSender;
    private final SendWelcomeEmailEmployee sendWelcomeEmailEmployee;
    private final SendWelcomeEmailCustomer sendWelcomeEmailCustomer;
    private final SendEmailLoginCustomerRequest sendEmailLoginCustomerRequest;

    public EmailService(
            EmailSender emailSender,
            SendWelcomeEmailEmployee sendWelcomeEmailEmployee,
            SendWelcomeEmailCustomer sendWelcomeEmailCustomer,
            SendEmailLoginCustomerRequest sendEmailLoginCustomerRequest
    ) {
        this.emailSender = emailSender;
        this.sendWelcomeEmailEmployee = sendWelcomeEmailEmployee;
        this.sendWelcomeEmailCustomer = sendWelcomeEmailCustomer;
        this.sendEmailLoginCustomerRequest = sendEmailLoginCustomerRequest;
    }

    @Override
    public void sendWelcomeEmailEmployee(String recipient, String name) {
        emailSender.send(sendWelcomeEmailEmployee.createMessage(recipient, name));
    }

    @Override
    public void sendWelcomeEmailCustomer(String recipient, String name) {
        emailSender.send(sendWelcomeEmailCustomer.createMessage(recipient, name));
    }

    @Override
    public void sendCustomerLoginCode(
            String recipient,
            String name,
            String code,
            Instant expiresAt
    ) {
        emailSender.send(sendEmailLoginCustomerRequest.createMessage(
                recipient,
                name,
                code,
                expiresAt
        ));
    }
}
