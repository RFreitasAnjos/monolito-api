package br.com.pasteldahora.notification.infrastrucuture.email;

import br.com.pasteldahora.notification.application.port.out.EmailTemplateRenderer;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

@Component
public class ThymeleafEmailTemplateRenderer implements EmailTemplateRenderer {

    private static final Locale BRAZILIAN_PORTUGUESE = Locale.forLanguageTag("pt-BR");

    private final SpringTemplateEngine templateEngine;

    public ThymeleafEmailTemplateRenderer(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String render(String template, Map<String, Object> variables) {
        Context context = new Context(BRAZILIAN_PORTUGUESE);
        context.setVariables(variables);
        return templateEngine.process(template, context);
    }
}
