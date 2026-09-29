package br.com.pasteldahora.catalog.adapter.out.persistence;

import br.com.pasteldahora.catalog.application.port.out.CategoryRepositoryPort;
import br.com.pasteldahora.catalog.domain.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository repository;

    CategoryPersistenceAdapter(SpringDataCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category save(Category category) {
        return toDomain(repository.save(toEntity(category)));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findById(id).map(CategoryPersistenceAdapter::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return repository.findAllByOrderByNameAsc().stream()
                .map(CategoryPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public List<Category> findActive() {
        return repository.findByActiveTrueOrderByNameAsc().stream()
                .map(CategoryPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return repository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    private static CategoryJpaEntity toEntity(Category category) {
        return new CategoryJpaEntity(
                category.getId(),
                category.getName(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getCreatedBy(),
                category.getUpdatedBy(),
                category.getDeactivatedAt(),
                category.getDeactivatedBy(),
                category.getVersion()
        );
    }

    private static Category toDomain(CategoryJpaEntity entity) {
        return Category.restore(
                entity.getId(),
                entity.getName(),
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
