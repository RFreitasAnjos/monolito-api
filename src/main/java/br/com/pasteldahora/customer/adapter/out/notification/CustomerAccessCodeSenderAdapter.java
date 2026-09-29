package br.com.pasteldahora.customer.adapter.out.notification;

import br.com.pasteldahora.customer.application.port.out.CustomerAccessCodeSenderPort;
import br.com.pasteldahora.customer.domain.exception.CustomerAccessChannelUnavailableException;
import br.com.pasteldahora.customer.domain.model.Customer;
import br.com.pasteldahora.customer.domain.model.CustomerAccessChannel;
import br.com.pasteldahora.notification.application.service.EmailUseCases;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CustomerAccessCodeSenderAdapter implements CustomerAccessCodeSenderPort {

    private final EmailUseCases emailUseCases;
    private final RestClient restClient;
    private final String whatsappWebhookUrl;
    private final String whatsappBearerToken;

    public CustomerAccessCodeSenderAdapter(
            EmailUseCases emailUseCases,
            @Value("${app.customer.whatsapp.webhook-url:}") String whatsappWebhookUrl,
            @Value("${app.customer.whatsapp.bearer-token:}") String whatsappBearerToken
    ) {
        this.emailUseCases = emailUseCases;
        this.restClient = RestClient.create();
        this.whatsappWebhookUrl = whatsappWebhookUrl;
        this.whatsappBearerToken = whatsappBearerToken;
    }

    @Override
    public void send(
            Customer customer,
            CustomerAccessChannel channel,
            String code,
            Instant expiresAt
    ) {
        if (channel == CustomerAccessChannel.EMAIL) {
            emailUseCases.sendCustomerLoginCode(
                    customer.getEmail(),
                    customer.getName(),
                    code,
                    expiresAt
            );
            return;
        }
        sendWhatsApp(customer, code, expiresAt);
    }

    private void sendWhatsApp(Customer customer, String code, Instant expiresAt) {
        if (whatsappWebhookUrl.isBlank()) {
            throw new CustomerAccessChannelUnavailableException("WHATSAPP");
        }

        RestClient.RequestBodySpec request = restClient.post()
                .uri(whatsappWebhookUrl);
        if (!whatsappBearerToken.isBlank()) {
            request.header("Authorization", "Bearer " + whatsappBearerToken);
        }
        try {
            request.body(new WhatsAppMessage(
                            customer.getPhone(),
                            "Seu código de acesso Pastel da Hora é %s. Válido até %s. "
                                    .formatted(
                                            code,
                                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                                    .withZone(ZoneId.of("America/Sao_Paulo"))
                                                    .format(expiresAt)
                                    )
                                    + "Não compartilhe este código."
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new CustomerAccessChannelUnavailableException("WHATSAPP", exception);
        }
    }

    private record WhatsAppMessage(String phone, String message) {
    }
}
