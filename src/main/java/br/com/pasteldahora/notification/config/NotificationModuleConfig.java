package br.com.pasteldahora.notification.config;

import br.com.pasteldahora.notification.application.port.out.EmailSender;
import br.com.pasteldahora.notification.application.port.out.EmailTemplateRenderer;
import br.com.pasteldahora.notification.application.usecases.SendEmailLoginCustomerRequest;
import br.com.pasteldahora.notification.application.usecases.SendWelcomeEmailCustomer;
import br.com.pasteldahora.notification.application.usecases.SendWelcomeEmailEmployee;
import br.com.pasteldahora.notification.infrastrucuture.email.JavaMailEmailSender;
import br.com.pasteldahora.notification.infrastrucuture.email.LoggingEmailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.ZoneId;

@Configuration(proxyBeanMethods = false)
public class NotificationModuleConfig {

    @Bean
    SendWelcomeEmailEmployee sendWelcomeEmailEmployee(
            EmailTemplateRenderer templateRenderer
    ) {
        return new SendWelcomeEmailEmployee(templateRenderer);
    }

    @Bean
    SendWelcomeEmailCustomer sendWelcomeEmailCustomer(
            EmailTemplateRenderer templateRenderer
    ) {
        return new SendWelcomeEmailCustomer(templateRenderer);
    }

    @Bean
    SendEmailLoginCustomerRequest sendEmailLoginCustomerRequest(
            EmailTemplateRenderer templateRenderer,
            @org.springframework.beans.factory.annotation.Value(
                    "${app.notification.email.zone-id:America/Sao_Paulo}"
            )
            String zoneId
    ) {
        return new SendEmailLoginCustomerRequest(templateRenderer, ZoneId.of(zoneId));
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "app.notification.email",
            name = "enabled",
            havingValue = "true"
    )
    EmailSender javaMailEmailSender(JavaMailSender javaMailSender) {
        return new JavaMailEmailSender(javaMailSender);
    }

    @Bean
    @ConditionalOnMissingBean(EmailSender.class)
    EmailSender loggingEmailSender() {
        return new LoggingEmailSender();
    }
}
