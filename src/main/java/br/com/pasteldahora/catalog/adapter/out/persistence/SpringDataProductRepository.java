package br.com.pasteldahora.catalog.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;

interface SpringDataProductRepository
        extends JpaRepository<ProductJpaEntity, UUID>, JpaSpecificationExecutor<ProductJpaEntity> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    boolean existsByCategoryIdAndActiveTrue(UUID categoryId);

    List<ProductJpaEntity> findByActiveTrueAndInventoryPolicyOrderByNameAsc(
            InventoryPolicy inventoryPolicy
    );

    List<ProductJpaEntity> findByActiveTrueOrderByNameAsc();

    @Query("""
            select (count(product) > 0)
            from ProductJpaEntity product
            join product.ingredients ingredient
            where ingredient.ingredientId = :ingredientId
              and product.active = true
            """)
    boolean existsActiveProductUsingIngredient(@Param("ingredientId") UUID ingredientId);
}
