package br.com.pasteldahora.catalog.application.service;

import br.com.pasteldahora.catalog.application.port.in.CreateIngredientCommand;
import br.com.pasteldahora.catalog.application.port.in.IngredientUseCase;
import br.com.pasteldahora.catalog.application.port.in.UpdateIngredientCommand;
import br.com.pasteldahora.catalog.application.port.out.IngredientRepositoryPort;
import br.com.pasteldahora.catalog.application.port.out.ProductRepositoryPort;
import br.com.pasteldahora.catalog.domain.exception.ActiveProductsUsingIngredientException;
import br.com.pasteldahora.catalog.domain.exception.DuplicateIngredientException;
import br.com.pasteldahora.catalog.domain.exception.IngredientNotFoundException;
import br.com.pasteldahora.catalog.domain.model.Ingredient;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class IngredientApplicationService implements IngredientUseCase {

    private final IngredientRepositoryPort repository;
    private final ProductRepositoryPort productRepository;
    private final Clock clock;

    public IngredientApplicationService(
            IngredientRepositoryPort repository,
            ProductRepositoryPort productRepository,
            Clock clock
    ) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.clock = clock;
    }

    @Override
    public Ingredient create(CreateIngredientCommand command) {
        Ingredient ingredient = Ingredient.create(
                command.sku(),
                command.name(),
                command.description(),
                command.stockUnit(),
                command.actor(),
                Instant.now(clock)
        );
        if (repository.existsBySku(ingredient.getSku())) {
            throw new DuplicateIngredientException(ingredient.getSku());
        }
        return repository.save(ingredient);
    }

    @Override
    public Ingredient update(UpdateIngredientCommand command) {
        Ingredient ingredient = findById(command.id()).update(
                command.sku(),
                command.name(),
                command.description(),
                command.stockUnit(),
                command.actor(),
                Instant.now(clock)
        );
        if (repository.existsBySkuAndIdNot(ingredient.getSku(), ingredient.getId())) {
            throw new DuplicateIngredientException(ingredient.getSku());
        }
        return repository.save(ingredient);
    }

    @Override
    public Ingredient findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IngredientNotFoundException(id));
    }

    @Override
    public List<Ingredient> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Ingredient> findActive() {
        return repository.findActive();
    }

    @Override
    public void deactivate(UUID id, String actor) {
        if (productRepository.existsByIngredientIdAndActiveTrue(id)) {
            throw new ActiveProductsUsingIngredientException();
        }
        repository.save(findById(id).deactivate(actor, Instant.now(clock)));
    }

    @Override
    public void reactivate(UUID id, String actor) {
        repository.save(findById(id).reactivate(actor, Instant.now(clock)));
    }
}
