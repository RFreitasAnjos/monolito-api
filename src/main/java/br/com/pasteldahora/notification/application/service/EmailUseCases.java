package br.com.pasteldahora.notification.application.service;

import java.time.Instant;

public interface EmailUseCases {

    void sendWelcomeEmailEmployee(String recipient, String name);

    void sendWelcomeEmailCustomer(String recipient, String name);

    void sendCustomerLoginCode(
            String recipient,
            String name,
            String code,
            Instant expiresAt
    );
}
