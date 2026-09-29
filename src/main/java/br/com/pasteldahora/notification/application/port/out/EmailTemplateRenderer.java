package br.com.pasteldahora.notification.application.port.out;

import java.util.Map;

public interface EmailTemplateRenderer {

    String render(String template, Map<String, Object> variables);
}
