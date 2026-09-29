package br.com.pasteldahora.catalog.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Produto comercializado pela Pastel da Hora.
 *
 * <p>Este agregado concentra as regras básicas do catálogo e permanece
 * independente de Spring, JPA ou qualquer mecanismo de persistência.</p>
 */
public final class Product {

    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private final UUID id;
    private final String name;
    private final String description;
    private final boolean active;

    private Product(UUID id, String name, String description, boolean active) {
        this.id = Objects.requireNonNull(id, "O identificador do produto é obrigatório.");
        this.name = validateName(name);
        this.description = validateDescription(description);
        this.active = active;
    }

    /**
     * Cadastra um produto novo. Todo produto nasce ativo e disponível para uso
     * pelo restante do sistema.
     */
    public static Product create(String name, String description) {
        return new Product(UUID.randomUUID(), name, description, true);
    }

    /**
     * Reconstrói um produto previamente persistido sem gerar uma nova identidade.
     */
    public static Product restore(UUID id, String name, String description, boolean active) {
        return new Product(id, name, description, active);
    }

    /**
     * Altera apenas os dados descritivos. O status é controlado pelas operações
     * específicas de ativação e desativação.
     */
    public Product update(String name, String description) {
        return new Product(id, name, description, active);
    }

    public Product deactivate() {
        if (!active) {
            return this;
        }
        return new Product(id, name, description, false);
    }

    public Product reactivate() {
        if (active) {
            return this;
        }
        return new Product(id, name, description, true);
    }

    private static String validateName(String name) {
        String normalizedName = requireText(name, "O nome do produto é obrigatório.").trim();
        if (normalizedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "O nome do produto deve possuir no máximo " + MAX_NAME_LENGTH + " caracteres."
            );
        }
        return normalizedName;
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalizedDescription = description.trim();
        if (normalizedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "A descrição do produto deve possuir no máximo "
                            + MAX_DESCRIPTION_LENGTH + " caracteres."
            );
        }
        return normalizedDescription;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}
