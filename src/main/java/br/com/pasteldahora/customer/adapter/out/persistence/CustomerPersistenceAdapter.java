package br.com.pasteldahora.customer.adapter.out.persistence;

import br.com.pasteldahora.customer.application.port.out.CustomerRepositoryPort;
import br.com.pasteldahora.customer.domain.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository repository;

    CustomerPersistenceAdapter(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer save(Customer customer) {
        return toDomain(repository.save(toEntity(customer)));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return repository.findById(id).map(CustomerPersistenceAdapter::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAllByOrderByNameAsc()
                .stream()
                .map(CustomerPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email.trim().toLowerCase(java.util.Locale.ROOT))
                .map(CustomerPersistenceAdapter::toDomain);
    }

    private static CustomerJpaEntity toEntity(Customer customer) {
        return new CustomerJpaEntity(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.getCreatedBy(),
                customer.getUpdatedBy(),
                customer.getDeactivatedAt(),
                customer.getDeactivatedBy(),
                customer.getVersion()
        );
    }

    private static Customer toDomain(CustomerJpaEntity entity) {
        return Customer.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
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
