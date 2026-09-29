package br.com.pasteldahora.customer.adapter.out.security;

import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationHashPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class CustomerAuthenticationHashAdapter implements CustomerAuthenticationHashPort {

    private final PasswordEncoder passwordEncoder;

    public CustomerAuthenticationHashAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hashCode(String code) {
        return passwordEncoder.encode(code);
    }

    @Override
    public boolean matchesCode(String code, String hash) {
        return passwordEncoder.matches(code, hash);
    }

    @Override
    public String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }
}
