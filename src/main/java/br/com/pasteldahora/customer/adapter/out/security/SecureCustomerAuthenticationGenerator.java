package br.com.pasteldahora.customer.adapter.out.security;

import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationGeneratorPort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecureCustomerAuthenticationGenerator
        implements CustomerAuthenticationGeneratorPort {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateCode() {
        return "%06d".formatted(secureRandom.nextInt(1_000_000));
    }

    @Override
    public String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
