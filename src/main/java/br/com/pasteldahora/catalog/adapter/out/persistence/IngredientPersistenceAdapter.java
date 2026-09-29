package br.com.pasteldahora.catalog.adapter.out.persistence;

import br.com.pasteldahora.catalog.application.port.out.IngredientRepositoryPort;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IngredientPersistenceAdapter implements IngredientRepositoryPort {

    private final SpringDataIngredientRepository repository;

    IngredientPersistenceAdapter(SpringDataIngredientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        return toDomain(repository.save(toEntity(ingredient)));
    }

    @Override
    public Optional<Ingredient> findById(UUID id) {
        return repository.findById(id).map(IngredientPersistenceAdapter::toDomain);
    }

    @Override
    public List<Ingredient> findAll() {
        return repository.findAllByOrderByNameAsc().stream()
                .map(IngredientPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public List<Ingredient> findActive() {
        return repository.findByActiveTrueOrderByNameAsc().stream()
                .map(IngredientPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySku(String sku) {
        return repository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, UUID id) {
        return repository.existsBySkuAndIdNot(sku, id);
    }

    private static IngredientJpaEntity toEntity(Ingredient ingredient) {
        return new IngredientJpaEntity(
                ingredient.getId(),
                ingredient.getSku(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getStockUnit(),
                ingredient.isActive(),
                ingredient.getCreatedAt(),
                ingredient.getUpdatedAt(),
                ingredient.getCreatedBy(),
                ingredient.getUpdatedBy(),
                ingredient.getDeactivatedAt(),
                ingredient.getDeactivatedBy(),
                ingredient.getVersion()
        );
    }

    private static Ingredient toDomain(IngredientJpaEntity entity) {
        return Ingredient.restore(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getStockUnit(),
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
