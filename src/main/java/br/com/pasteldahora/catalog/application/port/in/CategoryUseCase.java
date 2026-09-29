package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryUseCase {

    Category create(CreateCategoryCommand command);

    Category update(UpdateCategoryCommand command);

    Category findById(UUID id);

    List<Category> findAll();

    List<Category> findActive();

    void deactivate(UUID id, String actor);

    void reactivate(UUID id, String actor);
}
