package br.com.pasteldahora.customer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataCustomerSessionRepository
        extends JpaRepository<CustomerSessionJpaEntity, UUID> {

    Optional<CustomerSessionJpaEntity> findByTokenHash(String tokenHash);
}
