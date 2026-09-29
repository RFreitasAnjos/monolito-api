package br.com.pasteldahora.catalog.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Category {

    private static final int MAX_NAME_LENGTH = 80;
    private static final int MAX_ACTOR_LENGTH = 160;

    private final UUID id;
    private final String name;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final Instant deactivatedAt;
    private final String deactivatedBy;
    private final long version;

    private Category(
            UUID id,
            String name,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "O identificador da categoria é obrigatório.");
        this.name = validateName(name);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = validateUpdatedAt(createdAt, updatedAt);
        this.createdBy = validateActor(createdBy);
        this.updatedBy = validateActor(updatedBy);
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = validateDeactivation(
                active,
                createdAt,
                updatedAt,
                deactivatedAt,
                deactivatedBy
        );
        if (version < 0) {
            throw new IllegalArgumentException("A versão da categoria não pode ser negativa.");
        }
        this.version = version;
    }

    public static Category create(String name, String actor, Instant now) {
        return new Category(
                UUID.randomUUID(), name, true, now, now, actor, actor, null, null, 0
        );
    }

    public static Category restore(
            UUID id,
            String name,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        return new Category(
                id, name, active, createdAt, updatedAt, createdBy, updatedBy,
                deactivatedAt, deactivatedBy, version
        );
    }

    public Category update(String name, String actor, Instant now) {
        return new Category(
                id, name, active, createdAt, now, createdBy, actor,
                deactivatedAt, deactivatedBy, version
        );
    }

    public Category deactivate(String actor, Instant now) {
        if (!active) {
            return this;
        }
        return new Category(
                id, name, false, createdAt, now, createdBy, actor, now, actor, version
        );
    }

    public Category reactivate(String actor, Instant now) {
        if (active) {
            return this;
        }
        return new Category(
                id, name, true, createdAt, now, createdBy, actor, null, null, version
        );
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }
        String normalized = name.trim();
        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "O nome da categoria deve possuir no máximo 80 caracteres."
            );
        }
        return normalized;
    }

    private static Instant validateUpdatedAt(Instant createdAt, Instant updatedAt) {
        Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("A atualização não pode ser anterior à criação.");
        }
        return updatedAt;
    }

    private static String validateActor(String actor) {
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("O responsável pela operação é obrigatório.");
        }
        String normalized = actor.trim();
        if (normalized.length() > MAX_ACTOR_LENGTH) {
            throw new IllegalArgumentException(
                    "O responsável deve possuir no máximo 160 caracteres."
            );
        }
        return normalized;
    }

    private static String validateDeactivation(
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            Instant deactivatedAt,
            String deactivatedBy
    ) {
        if (active) {
            if (deactivatedAt != null || deactivatedBy != null) {
                throw new IllegalArgumentException(
                        "Uma categoria ativa não pode possuir dados de desativação."
                );
            }
            return null;
        }
        Objects.requireNonNull(deactivatedAt, "A data de desativação é obrigatória.");
        if (deactivatedAt.isBefore(createdAt) || deactivatedAt.isAfter(updatedAt)) {
            throw new IllegalArgumentException("A data de desativação é inconsistente.");
        }
        return validateActor(deactivatedBy);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
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
