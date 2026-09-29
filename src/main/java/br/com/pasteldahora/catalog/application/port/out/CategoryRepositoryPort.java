package br.com.pasteldahora.catalog.application.port.out;

import br.com.pasteldahora.catalog.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    List<Category> findAll();

    List<Category> findActive();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
