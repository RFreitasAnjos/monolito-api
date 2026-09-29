package br.com.pasteldahora.customer.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Customer {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,15}$");

    private final UUID id;
    private final String name;
    private final String email;
    private final String phone;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final Instant deactivatedAt;
    private final String deactivatedBy;
    private final long version;

    private Customer(
            UUID id,
            String name,
            String email,
            String phone,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do cliente é obrigatório.");
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.phone = validatePhone(phone);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = validateUpdatedAt(createdAt, updatedAt);
        this.createdBy = validateActor(createdBy);
        this.updatedBy = validateActor(updatedBy);
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        validateDeactivation();
        if (version < 0) {
            throw new IllegalArgumentException("A versão do cliente não pode ser negativa.");
        }
        this.version = version;
    }

    public static Customer create(
            String name,
            String email,
            String phone,
            String actor,
            Instant now
    ) {
        return new Customer(
                UUID.randomUUID(),
                name,
                email,
                phone,
                true,
                now,
                now,
                actor,
                actor,
                null,
                null,
                0
        );
    }

    public static Customer restore(
            UUID id,
            String name,
            String email,
            String phone,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        return new Customer(
                id,
                name,
                email,
                phone,
                active,
                createdAt,
                updatedAt,
                createdBy,
                updatedBy,
                deactivatedAt,
                deactivatedBy,
                version
        );
    }

    public Customer update(
            String name,
            String email,
            String phone,
            String actor,
            Instant now
    ) {
        return new Customer(
                id,
                name,
                email,
                phone,
                active,
                createdAt,
                now,
                createdBy,
                actor,
                deactivatedAt,
                deactivatedBy,
                version
        );
    }

    public Customer deactivate(String actor, Instant now) {
        if (!active) {
            return this;
        }
        return new Customer(
                id, name, email, phone, false,
                createdAt, now, createdBy, actor, now, actor, version
        );
    }

    public Customer reactivate(String actor, Instant now) {
        if (active) {
            return this;
        }
        return new Customer(
                id, name, email, phone, true,
                createdAt, now, createdBy, actor, null, null, version
        );
    }

    private void validateDeactivation() {
        if (active) {
            if (deactivatedAt != null || deactivatedBy != null) {
                throw new IllegalArgumentException(
                        "Cliente ativo não pode possuir dados de desativação."
                );
            }
            return;
        }
        Objects.requireNonNull(deactivatedAt, "A data de desativação é obrigatória.");
        validateActor(deactivatedBy);
    }

    private static String validateName(String value) {
        String normalized = requireText(value, "O nome do cliente é obrigatório.").trim();
        if (normalized.length() > 120) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 120 caracteres.");
        }
        return normalized;
    }

    private static String validateEmail(String value) {
        String normalized = requireText(value, "O e-mail do cliente é obrigatório.")
                .trim()
                .toLowerCase(Locale.ROOT);
        if (normalized.length() > 160 || !EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("O e-mail do cliente é inválido.");
        }
        return normalized;
    }

    private static String validatePhone(String value) {
        String digits = requireText(value, "O telefone do cliente é obrigatório.")
                .replaceAll("\\D", "");
        if (!PHONE_PATTERN.matcher(digits).matches()) {
            throw new IllegalArgumentException("O telefone deve possuir entre 10 e 15 dígitos.");
        }
        return digits;
    }

    private static Instant validateUpdatedAt(Instant createdAt, Instant updatedAt) {
        Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("A atualização não pode ser anterior à criação.");
        }
        return updatedAt;
    }

    private static String validateActor(String value) {
        String normalized = requireText(value, "O responsável é obrigatório.").trim();
        if (normalized.length() > 160) {
            throw new IllegalArgumentException(
                    "O responsável deve possuir no máximo 160 caracteres."
            );
        }
        return normalized;
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

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    public long getVersion() {
        return version;
    }
}
