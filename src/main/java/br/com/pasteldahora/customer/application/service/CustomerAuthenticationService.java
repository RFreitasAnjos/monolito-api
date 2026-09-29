package br.com.pasteldahora.customer.application.service;

import br.com.pasteldahora.customer.application.port.in.CustomerAuthentication;
import br.com.pasteldahora.customer.application.port.in.CustomerAuthenticationUseCase;
import br.com.pasteldahora.customer.application.port.in.RequestCustomerAccessCodeCommand;
import br.com.pasteldahora.customer.application.port.in.VerifyCustomerAccessCodeCommand;
import br.com.pasteldahora.customer.application.port.out.CustomerAccessCodeData;
import br.com.pasteldahora.customer.application.port.out.CustomerAccessCodeSenderPort;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationGeneratorPort;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationHashPort;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationRepositoryPort;
import br.com.pasteldahora.customer.application.port.out.CustomerRepositoryPort;
import br.com.pasteldahora.customer.application.port.out.CustomerSessionData;
import br.com.pasteldahora.customer.domain.exception.InvalidCustomerAccessCodeException;
import br.com.pasteldahora.customer.domain.model.Customer;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class CustomerAuthenticationService implements CustomerAuthenticationUseCase {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final CustomerRepositoryPort customerRepository;
    private final CustomerAuthenticationRepositoryPort authenticationRepository;
    private final CustomerAuthenticationHashPort hashPort;
    private final CustomerAuthenticationGeneratorPort generatorPort;
    private final CustomerAccessCodeSenderPort senderPort;
    private final Clock clock;
    private final Duration codeValidity;
    private final Duration requestCooldown;
    private final Duration sessionValidity;

    public CustomerAuthenticationService(
            CustomerRepositoryPort customerRepository,
            CustomerAuthenticationRepositoryPort authenticationRepository,
            CustomerAuthenticationHashPort hashPort,
            CustomerAuthenticationGeneratorPort generatorPort,
            CustomerAccessCodeSenderPort senderPort,
            Clock clock,
            Duration codeValidity,
            Duration requestCooldown,
            Duration sessionValidity
    ) {
        this.customerRepository = customerRepository;
        this.authenticationRepository = authenticationRepository;
        this.hashPort = hashPort;
        this.generatorPort = generatorPort;
        this.senderPort = senderPort;
        this.clock = clock;
        this.codeValidity = codeValidity;
        this.requestCooldown = requestCooldown;
        this.sessionValidity = sessionValidity;
    }

    @Override
    @Transactional
    public void requestAccessCode(RequestCustomerAccessCodeCommand command) {
        String email = normalizeEmail(command.email());
        Customer customer = customerRepository.findByEmail(email).orElse(null);
        if (customer == null || !customer.isActive()) {
            return;
        }

        Instant now = Instant.now(clock);
        boolean recentlyRequested = authenticationRepository.findLatestCode(customer.getId())
                .filter(code -> code.createdAt().plus(requestCooldown).isAfter(now))
                .isPresent();
        if (recentlyRequested) {
            return;
        }

        String code = generatorPort.generateCode();
        authenticationRepository.consumePendingCodes(customer.getId(), now);
        Instant expiresAt = now.plus(codeValidity);
        authenticationRepository.saveCode(new CustomerAccessCodeData(
                UUID.randomUUID(),
                customer.getId(),
                hashPort.hashCode(code),
                command.channel(),
                expiresAt,
                null,
                0,
                now,
                0
        ));
        senderPort.send(customer, command.channel(), code, expiresAt);
    }

    @Override
    @Transactional(noRollbackFor = InvalidCustomerAccessCodeException.class)
    public CustomerAuthentication verifyAccessCode(VerifyCustomerAccessCodeCommand command) {
        Instant now = Instant.now(clock);
        Customer customer = customerRepository.findByEmail(normalizeEmail(command.email()))
                .filter(Customer::isActive)
                .orElseThrow(InvalidCustomerAccessCodeException::new);
        CustomerAccessCodeData accessCode = authenticationRepository
                .findLatestCode(customer.getId())
                .orElseThrow(InvalidCustomerAccessCodeException::new);

        boolean unavailable = accessCode.consumedAt() != null
                || !accessCode.expiresAt().isAfter(now)
                || accessCode.failedAttempts() >= MAX_FAILED_ATTEMPTS;
        if (unavailable || !hashPort.matchesCode(validateCode(command.code()), accessCode.codeHash())) {
            if (!unavailable) {
                authenticationRepository.saveCode(accessCode.failAttempt());
            }
            throw new InvalidCustomerAccessCodeException();
        }

        authenticationRepository.saveCode(accessCode.consume(now));
        String token = generatorPort.generateToken();
        Instant expiresAt = now.plus(sessionValidity);
        authenticationRepository.saveSession(new CustomerSessionData(
                UUID.randomUUID(),
                customer.getId(),
                hashPort.hashToken(token),
                expiresAt,
                null,
                now,
                0
        ));
        return new CustomerAuthentication(customer, token, expiresAt);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer authenticate(String accessToken) {
        Instant now = Instant.now(clock);
        CustomerSessionData session = authenticationRepository
                .findSessionByTokenHash(hashPort.hashToken(requireToken(accessToken)))
                .filter(item -> item.revokedAt() == null && item.expiresAt().isAfter(now))
                .orElseThrow(InvalidCustomerAccessCodeException::new);
        return customerRepository.findById(session.customerId())
                .filter(Customer::isActive)
                .orElseThrow(InvalidCustomerAccessCodeException::new);
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        authenticationRepository.findSessionByTokenHash(hashPort.hashToken(requireToken(accessToken)))
                .filter(session -> session.revokedAt() == null)
                .ifPresent(session -> authenticationRepository.saveSession(
                        session.revoke(Instant.now(clock))
                ));
    }

    private static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String validateCode(String code) {
        if (code == null || !code.matches("\\d{6}")) {
            throw new InvalidCustomerAccessCodeException();
        }
        return code;
    }

    private static String requireToken(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidCustomerAccessCodeException();
        }
        return token;
    }
}
