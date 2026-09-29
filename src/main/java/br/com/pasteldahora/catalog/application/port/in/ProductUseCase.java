package br.com.pasteldahora.catalog.application.port.in;

import br.com.pasteldahora.catalog.domain.model.Product;

import java.util.UUID;

public interface ProductUseCase {

    Product create(CreateProductCommand command);

    Product update(UpdateProductCommand command);

    Product findById(UUID id);

    PageResult<Product> search(ProductSearchQuery query);

    void deactivate(UUID id, String actor);

    void reactivate(UUID id, String actor);
}
