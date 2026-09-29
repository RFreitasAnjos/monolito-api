package br.com.pasteldahora.catalog.application.port.in;

import java.util.UUID;

public record UpdateCategoryCommand(UUID id, String name, String actor) {
}
