package br.com.pasteldahora.employee.config;

import br.com.pasteldahora.employee.application.port.in.EmployeeUseCase;
import br.com.pasteldahora.employee.application.port.out.EmployeeRepositoryPort;
import br.com.pasteldahora.employee.application.port.out.PasswordHashPort;
import br.com.pasteldahora.employee.application.service.EmployeeApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Liga as portas aos adaptadores sem acoplar o núcleo ao framework.
 */
@Configuration(proxyBeanMethods = false)
public class EmployeeModuleConfig {

    @Bean
    EmployeeUseCase employeeUseCase(
            EmployeeRepositoryPort repository,
            PasswordHashPort passwordHashPort,
            Clock clock
    ) {
        return new EmployeeApplicationService(repository, passwordHashPort, clock);
    }

    @Bean
    Clock employeeClock() {
        return Clock.systemUTC();
    }

    @Bean
    ApplicationRunner employeeAdminBootstrap(
            EmployeeUseCase useCase,
            EmployeeRepositoryPort repository,
            @Value("${app.employee.bootstrap.name}") String name,
            @Value("${app.employee.bootstrap.cpf}") String cpf,
            @Value("${app.employee.bootstrap.email}") String email,
            @Value("${app.employee.bootstrap.password}") String password
    ) {
        return arguments -> {
            if (!repository.existsByEmail(email)) {
                useCase.create(new br.com.pasteldahora.employee.application.port.in.CreateEmployeeCommand(
                        name,
                        cpf,
                        email,
                        password,
                        br.com.pasteldahora.employee.domain.model.EmployeePosition.MANAGER,
                        br.com.pasteldahora.employee.domain.model.EmployeeAccessRole.ADMIN,
                        "system"
                ));
            }
        };
    }
}
