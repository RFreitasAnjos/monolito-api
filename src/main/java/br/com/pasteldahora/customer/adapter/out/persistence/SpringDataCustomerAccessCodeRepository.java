package br.com.pasteldahora.customer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataCustomerAccessCodeRepository
        extends JpaRepository<CustomerAccessCodeJpaEntity, UUID> {

    List<CustomerAccessCodeJpaEntity> findByCustomerIdAndConsumedAtIsNull(UUID customerId);

    Optional<CustomerAccessCodeJpaEntity>
    findFirstByCustomerIdAndConsumedAtIsNullOrderByCreatedAtDesc(UUID customerId);
}
