package br.com.pasteldahora.payment.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoSdkConfig {

    private final String accessToken;

    public MercadoPagoSdkConfig(@Value("${mercadopago.access-token:}") String accessToken) {
        this.accessToken = normalize(accessToken);
    }

    @PostConstruct
    void configureSdk() {
        if (accessToken.isBlank()) {
            throw new IllegalStateException(
                    "mercadopago.access-token must be configured through MERCADOPAGO_ACCESS_TOKEN"
            );
        }
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Bean
    PreferenceClient preferenceClient() {
        return new PreferenceClient();
    }

    @Bean
    PaymentClient paymentClient() {
        return new PaymentClient();
    }

    private static String normalize(String token) {
        if (token == null) {
            return "";
        }

        String normalized = token.trim();
        if (normalized.length() >= 2) {
            boolean doubleQuoted = normalized.startsWith("\"") && normalized.endsWith("\"");
            boolean singleQuoted = normalized.startsWith("'") && normalized.endsWith("'");
            if (doubleQuoted || singleQuoted) {
                normalized = normalized.substring(1, normalized.length() - 1).trim();
            }
        }
        return normalized;
    }
}
