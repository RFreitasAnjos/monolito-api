package br.com.pasteldahora.employee.domain.model;

import br.com.pasteldahora.employee.domain.validation.CpfValidator;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Funcionário e conta gerencial. O hash da senha nunca é exposto pelos adaptadores.
 */
public final class Employee {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    private final UUID id;
    private final String name;
    private final String cpf;
    private final String email;
    private final String passwordHash;
    private final EmployeePosition position;
    private final EmployeeAccessRole accessRole;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;
    private final Instant deactivatedAt;
    private final String deactivatedBy;
    private final long version;

    private Employee(
            UUID id,
            String name,
            String cpf,
            String email,
            String passwordHash,
            EmployeePosition position,
            EmployeeAccessRole accessRole,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "O identificador do funcionário é obrigatório.");
        this.name = validateName(name);
        this.cpf = CpfValidator.normalizeAndValidate(cpf);
        this.email = validateEmail(email);
        this.passwordHash = requireText(passwordHash, "O hash da senha é obrigatório.");
        this.position = Objects.requireNonNull(position, "O cargo do funcionário é obrigatório.");
        this.accessRole = Objects.requireNonNull(accessRole, "O perfil de acesso é obrigatório.");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação é obrigatória.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "A data de atualização é obrigatória.");
        this.createdBy = requireText(createdBy, "O responsável pela criação é obrigatório.");
        this.updatedBy = requireText(updatedBy, "O responsável pela atualização é obrigatório.");
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        this.version = version;
    }

    public static Employee create(
            String name,
            String cpf,
            String email,
            String passwordHash,
            EmployeePosition position,
            EmployeeAccessRole accessRole,
            String actor,
            Instant now
    ) {
        return new Employee(
                UUID.randomUUID(), name, cpf, email, passwordHash, position, accessRole,
                true, now, now, actor, actor, null, null, 0
        );
    }

    public static Employee restore(
            UUID id,
            String name,
            String cpf,
            String email,
            String passwordHash,
            EmployeePosition position,
            EmployeeAccessRole accessRole,
            boolean active,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            Instant deactivatedAt,
            String deactivatedBy,
            long version
    ) {
        return new Employee(
                id, name, cpf, email, passwordHash, position, accessRole, active,
                createdAt, updatedAt, createdBy, updatedBy, deactivatedAt, deactivatedBy, version
        );
    }

    public Employee update(
            String name,
            String email,
            EmployeePosition position,
            EmployeeAccessRole accessRole,
            String newPasswordHash,
            String actor,
            Instant now
    ) {
        String effectivePasswordHash = newPasswordHash == null ? passwordHash : newPasswordHash;
        return new Employee(
                id, name, cpf, email, effectivePasswordHash, position, accessRole, active,
                createdAt, now, createdBy, actor, deactivatedAt, deactivatedBy, version
        );
    }

    public Employee deactivate(String actor, Instant now) {
        if (!active) {
            return this;
        }
        return new Employee(
                id, name, cpf, email, passwordHash, position, accessRole, false,
                createdAt, now, createdBy, actor, now, actor, version
        );
    }

    public Employee reactivate(String actor, Instant now) {
        if (active) {
            return this;
        }
        return new Employee(
                id, name, cpf, email, passwordHash, position, accessRole, true,
                createdAt, now, createdBy, actor, null, null, version
        );
    }

    private static String validateName(String name) {
        String normalizedName = requireText(name, "O nome do funcionário é obrigatório.").trim();
        if (normalizedName.length() > 120) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 120 caracteres.");
        }
        return normalizedName;
    }

    private static String validateEmail(String email) {
        String normalizedEmail = requireText(email, "O e-mail do funcionário é obrigatório.")
                .trim()
                .toLowerCase(Locale.ROOT);
        if (normalizedEmail.length() > 160 || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("O e-mail do funcionário é inválido.");
        }
        return normalizedEmail;
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

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public EmployeePosition getPosition() {
        return position;
    }

    public EmployeeAccessRole getAccessRole() {
        return accessRole;
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
