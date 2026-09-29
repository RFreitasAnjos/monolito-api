package br.com.pasteldahora.customer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    List<CustomerJpaEntity> findAllByOrderByNameAsc();

    Optional<CustomerJpaEntity> findByEmail(String email);
}
