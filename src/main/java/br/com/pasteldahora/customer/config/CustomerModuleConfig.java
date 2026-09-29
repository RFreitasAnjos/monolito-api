package br.com.pasteldahora.customer.config;

import br.com.pasteldahora.customer.application.port.in.CustomerAuthenticationUseCase;
import br.com.pasteldahora.customer.application.port.in.CustomerUseCase;
import br.com.pasteldahora.customer.application.port.out.CustomerAccessCodeSenderPort;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationGeneratorPort;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationHashPort;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationRepositoryPort;
import br.com.pasteldahora.customer.application.port.out.CustomerRepositoryPort;
import br.com.pasteldahora.customer.application.service.CustomerAuthenticationService;
import br.com.pasteldahora.customer.application.service.CustomerApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class CustomerModuleConfig {

    @Bean
    CustomerUseCase customerUseCase(
            CustomerRepositoryPort repository,
            Clock clock
    ) {
        return new CustomerApplicationService(repository, clock);
    }

    @Bean
    CustomerAuthenticationUseCase customerAuthenticationUseCase(
            CustomerRepositoryPort customerRepository,
            CustomerAuthenticationRepositoryPort authenticationRepository,
            CustomerAuthenticationHashPort hashPort,
            CustomerAuthenticationGeneratorPort generatorPort,
            CustomerAccessCodeSenderPort senderPort,
            Clock clock,
            @Value("${app.customer.auth.code-validity:PT10M}") Duration codeValidity,
            @Value("${app.customer.auth.request-cooldown:PT1M}") Duration requestCooldown,
            @Value("${app.customer.auth.session-validity:PT24H}") Duration sessionValidity
    ) {
        return new CustomerAuthenticationService(
                customerRepository,
                authenticationRepository,
                hashPort,
                generatorPort,
                senderPort,
                clock,
                codeValidity,
                requestCooldown,
                sessionValidity
        );
    }
}
