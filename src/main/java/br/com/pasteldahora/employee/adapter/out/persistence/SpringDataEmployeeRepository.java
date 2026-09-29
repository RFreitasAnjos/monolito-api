package br.com.pasteldahora.employee.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

interface SpringDataEmployeeRepository
        extends JpaRepository<EmployeeJpaEntity, UUID>, JpaSpecificationExecutor<EmployeeJpaEntity> {

    Optional<EmployeeJpaEntity> findByEmailIgnoreCase(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}
