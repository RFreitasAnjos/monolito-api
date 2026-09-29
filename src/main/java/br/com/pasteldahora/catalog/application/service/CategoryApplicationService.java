package br.com.pasteldahora.catalog.application.service;

import br.com.pasteldahora.catalog.application.port.in.CategoryUseCase;
import br.com.pasteldahora.catalog.application.port.in.CreateCategoryCommand;
import br.com.pasteldahora.catalog.application.port.in.UpdateCategoryCommand;
import br.com.pasteldahora.catalog.application.port.out.CategoryRepositoryPort;
import br.com.pasteldahora.catalog.application.port.out.ProductRepositoryPort;
import br.com.pasteldahora.catalog.domain.exception.ActiveProductsInCategoryException;
import br.com.pasteldahora.catalog.domain.exception.CategoryNotFoundException;
import br.com.pasteldahora.catalog.domain.exception.DuplicateCategoryException;
import br.com.pasteldahora.catalog.domain.model.Category;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class CategoryApplicationService implements CategoryUseCase {

    private final CategoryRepositoryPort repository;
    private final ProductRepositoryPort productRepository;
    private final Clock clock;

    public CategoryApplicationService(
            CategoryRepositoryPort repository,
            ProductRepositoryPort productRepository,
            Clock clock
    ) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.clock = clock;
    }

    @Override
    public Category create(CreateCategoryCommand command) {
        Category category = Category.create(command.name(), command.actor(), Instant.now(clock));
        if (repository.existsByName(category.getName())) {
            throw new DuplicateCategoryException(category.getName());
        }
        return repository.save(category);
    }

    @Override
    public Category update(UpdateCategoryCommand command) {
        Category category = findById(command.id()).update(
                command.name(),
                command.actor(),
                Instant.now(clock)
        );
        if (repository.existsByNameAndIdNot(category.getName(), category.getId())) {
            throw new DuplicateCategoryException(category.getName());
        }
        return repository.save(category);
    }

    @Override
    public Category findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Category> findActive() {
        return repository.findActive();
    }

    @Override
    public void deactivate(UUID id, String actor) {
        if (productRepository.existsByCategoryIdAndActiveTrue(id)) {
            throw new ActiveProductsInCategoryException();
        }
        repository.save(findById(id).deactivate(actor, Instant.now(clock)));
    }

    @Override
    public void reactivate(UUID id, String actor) {
        repository.save(findById(id).reactivate(actor, Instant.now(clock)));
    }
}
