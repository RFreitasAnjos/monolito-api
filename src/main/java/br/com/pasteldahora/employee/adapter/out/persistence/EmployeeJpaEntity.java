package br.com.pasteldahora.employee.adapter.out.persistence;

import br.com.pasteldahora.employee.domain.model.EmployeeAccessRole;
import br.com.pasteldahora.employee.domain.model.EmployeePosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employees")
class EmployeeJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmployeePosition position;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_role", nullable = false, length = 30)
    private EmployeeAccessRole accessRole;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 160)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 160)
    private String updatedBy;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Column(name = "deactivated_by", length = 160)
    private String deactivatedBy;

    @Version
    private long version;

    protected EmployeeJpaEntity() {
    }

    EmployeeJpaEntity(
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
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.passwordHash = passwordHash;
        this.position = position;
        this.accessRole = accessRole;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getCpf() {
        return cpf;
    }

    String getEmail() {
        return email;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    EmployeePosition getPosition() {
        return position;
    }

    EmployeeAccessRole getAccessRole() {
        return accessRole;
    }

    boolean isActive() {
        return active;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }

    String getCreatedBy() {
        return createdBy;
    }

    String getUpdatedBy() {
        return updatedBy;
    }

    Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    String getDeactivatedBy() {
        return deactivatedBy;
    }

    long getVersion() {
        return version;
    }
}
