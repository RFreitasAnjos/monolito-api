package br.com.pasteldahora.employee.adapter.out.persistence;

import br.com.pasteldahora.employee.application.port.in.EmployeeSearchQuery;
import br.com.pasteldahora.employee.application.port.in.PageResult;
import br.com.pasteldahora.employee.application.port.out.EmployeeRepositoryPort;
import br.com.pasteldahora.employee.domain.model.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Traduz o contrato de persistência do módulo para Spring Data JPA.
 */
@Repository
public class EmployeePersistenceAdapter implements EmployeeRepositoryPort {

    private final SpringDataEmployeeRepository repository;

    EmployeePersistenceAdapter(SpringDataEmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Employee save(Employee employee) {
        return toDomain(repository.save(toEntity(employee)));
    }

    @Override
    public PageResult<Employee> search(EmployeeSearchQuery query) {
        PageRequest pageRequest = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Direction.ASC, "name")
        );
        Page<Employee> page = repository.findAll(specification(query), pageRequest)
                .map(EmployeePersistenceAdapter::toDomain);
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return repository.findById(id).map(EmployeePersistenceAdapter::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(EmployeePersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf.replaceAll("\\D", ""));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return repository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    private static Specification<EmployeeJpaEntity> specification(EmployeeSearchQuery query) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!query.term().isBlank()) {
                String term = "%" + query.term().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("name")), term),
                        builder.like(builder.lower(root.get("email")), term),
                        builder.like(root.get("cpf"), "%" + query.term().replaceAll("\\D", "") + "%")
                ));
            }
            if (query.active() != null) {
                predicates.add(builder.equal(root.get("active"), query.active()));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static EmployeeJpaEntity toEntity(Employee employee) {
        return new EmployeeJpaEntity(
                employee.getId(),
                employee.getName(),
                employee.getCpf(),
                employee.getEmail(),
                employee.getPasswordHash(),
                employee.getPosition(),
                employee.getAccessRole(),
                employee.isActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt(),
                employee.getCreatedBy(),
                employee.getUpdatedBy(),
                employee.getDeactivatedAt(),
                employee.getDeactivatedBy(),
                employee.getVersion()
        );
    }

    private static Employee toDomain(EmployeeJpaEntity entity) {
        return Employee.restore(
                entity.getId(),
                entity.getName(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getPosition(),
                entity.getAccessRole(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getDeactivatedAt(),
                entity.getDeactivatedBy(),
                entity.getVersion()
        );
    }
}
