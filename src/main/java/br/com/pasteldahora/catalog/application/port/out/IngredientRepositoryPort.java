package br.com.pasteldahora.catalog.application.port.out;

import br.com.pasteldahora.catalog.domain.model.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepositoryPort {

    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(UUID id);

    List<Ingredient> findAll();

    List<Ingredient> findActive();

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
