package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.Ingredient;

import java.util.List;
import java.util.UUID;

public interface IngredientUseCase {

    Ingredient create(CreateIngredientCommand command);

    Ingredient update(UpdateIngredientCommand command);

    Ingredient findById(UUID id);

    List<Ingredient> findAll();

    List<Ingredient> findActive();

    void deactivate(UUID id, String actor);

    void reactivate(UUID id, String actor);
}
