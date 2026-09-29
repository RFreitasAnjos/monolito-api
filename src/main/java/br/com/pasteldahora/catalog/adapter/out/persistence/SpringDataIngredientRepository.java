package br.com.pasteldahora.catalog.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataIngredientRepository extends JpaRepository<IngredientJpaEntity, UUID> {

    List<IngredientJpaEntity> findAllByOrderByNameAsc();

    List<IngredientJpaEntity> findByActiveTrueOrderByNameAsc();

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
