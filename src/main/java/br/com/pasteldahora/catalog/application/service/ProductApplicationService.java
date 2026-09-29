package br.com.pasteldahora.catalog.application.service;

import br.com.pasteldahora.catalog.application.port.in.CatalogItemQuery;
import br.com.pasteldahora.catalog.application.port.in.CreateProductCommand;
import br.com.pasteldahora.catalog.application.port.in.PageResult;
import br.com.pasteldahora.catalog.application.port.in.ProductSearchQuery;
import br.com.pasteldahora.catalog.application.port.in.ProductUseCase;
import br.com.pasteldahora.catalog.application.port.in.StockableProductData;
import br.com.pasteldahora.catalog.application.port.in.StockableCatalogItemData;
import br.com.pasteldahora.catalog.application.port.in.SellableProductData;
import br.com.pasteldahora.catalog.application.port.in.UpdateProductCommand;
import br.com.pasteldahora.catalog.application.port.out.CategoryRepositoryPort;
import br.com.pasteldahora.catalog.application.port.out.IngredientRepositoryPort;
import br.com.pasteldahora.catalog.application.port.out.ProductRepositoryPort;
import br.com.pasteldahora.catalog.domain.exception.CategoryNotFoundException;
import br.com.pasteldahora.catalog.domain.exception.DuplicateProductException;
import br.com.pasteldahora.catalog.domain.exception.ProductNotFoundException;
import br.com.pasteldahora.catalog.domain.model.Category;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.Product;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;
import br.com.pasteldahora.catalog.domain.model.CatalogItemType;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public final class ProductApplicationService implements ProductUseCase, CatalogItemQuery {

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final IngredientRepositoryPort ingredientRepository;
    private final Clock clock;

    public ProductApplicationService(
            ProductRepositoryPort productRepository,
            CategoryRepositoryPort categoryRepository,
            IngredientRepositoryPort ingredientRepository,
            Clock clock
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.clock = clock;
    }

    @Override
    public Product create(CreateProductCommand command) {
        requireActiveCategory(command.categoryId());
        List<ProductIngredient> ingredients = requireActiveIngredients(command.ingredients());
        Product product = Product.create(
                command.sku(),
                command.name(),
                command.description(),
                command.categoryId(),
                command.salePrice(),
                command.inventoryPolicy(),
                command.customizable(),
                ingredients,
                command.actor(),
                Instant.now(clock)
        );
        if (productRepository.existsBySku(product.getSku())) {
            throw new DuplicateProductException(product.getSku());
        }
        return productRepository.save(product);
    }

    @Override
    public Product update(UpdateProductCommand command) {
        Product product = findById(command.id());
        requireActiveCategory(command.categoryId());
        List<ProductIngredient> ingredients = requireActiveIngredients(command.ingredients());
        Product updated = product.update(
                command.sku(),
                command.name(),
                command.description(),
                command.categoryId(),
                command.salePrice(),
                command.inventoryPolicy(),
                command.customizable(),
                ingredients,
                command.actor(),
                Instant.now(clock)
        );
        if (productRepository.existsBySkuAndIdNot(updated.getSku(), updated.getId())) {
            throw new DuplicateProductException(updated.getSku());
        }
        return productRepository.save(updated);
    }

    @Override
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public PageResult<Product> search(ProductSearchQuery query) {
        return productRepository.search(query);
    }

    @Override
    public void deactivate(UUID id, String actor) {
        productRepository.save(findById(id).deactivate(actor, Instant.now(clock)));
    }

    @Override
    public void reactivate(UUID id, String actor) {
        Product product = findById(id);
        requireActiveCategory(product.getCategoryId());
        requireActiveIngredients(product.getIngredients());
        productRepository.save(product.reactivate(actor, Instant.now(clock)));
    }

    @Override
    public StockableProductData findStockableProduct(UUID productId) {
        Product product = findById(productId);
        return new StockableProductData(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getInventoryPolicy(),
                product.isActive()
        );
    }

    @Override
    public List<StockableProductData> findActiveDirectStockProducts() {
        return productRepository.findActiveByInventoryPolicy(InventoryPolicy.DIRECT_STOCK)
                .stream()
                .map(product -> new StockableProductData(
                        product.getId(),
                        product.getSku(),
                        product.getName(),
                        product.getInventoryPolicy(),
                        product.isActive()
                ))
                .toList();
    }

    @Override
    public StockableCatalogItemData findStockableItem(UUID itemId, CatalogItemType itemType) {
        return switch (itemType) {
            case PRODUCT -> toStockableCatalogItem(findById(itemId));
            case INGREDIENT -> toStockableCatalogItem(
                    ingredientRepository.findById(itemId)
                            .orElseThrow(() -> new br.com.pasteldahora.catalog.domain.exception.IngredientNotFoundException(itemId))
            );
        };
    }

    @Override
    public List<StockableCatalogItemData> findActiveStockableItems() {
        List<StockableCatalogItemData> products = productRepository
                .findActiveByInventoryPolicy(InventoryPolicy.DIRECT_STOCK)
                .stream()
                .map(ProductApplicationService::toStockableCatalogItem)
                .toList();
        List<StockableCatalogItemData> ingredients = ingredientRepository.findActive()
                .stream()
                .map(ProductApplicationService::toStockableCatalogItem)
                .toList();
        return java.util.stream.Stream.concat(products.stream(), ingredients.stream())
                .sorted(java.util.Comparator.comparing(StockableCatalogItemData::name))
                .toList();
    }

    @Override
    public SellableProductData findSellableProduct(UUID productId) {
        return toSellableProduct(findById(productId));
    }

    @Override
    public List<SellableProductData> findActiveSellableProducts() {
        return productRepository.findActive()
                .stream()
                .map(ProductApplicationService::toSellableProduct)
                .toList();
    }

    private static SellableProductData toSellableProduct(Product product) {
        return new SellableProductData(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getSalePrice(),
                product.getInventoryPolicy(),
                product.isActive()
        );
    }

    private static StockableCatalogItemData toStockableCatalogItem(Product product) {
        return new StockableCatalogItemData(
                product.getId(),
                product.getSku(),
                product.getName(),
                CatalogItemType.PRODUCT,
                UnitOfMeasure.UNIT,
                product.isActive()
        );
    }

    private static StockableCatalogItemData toStockableCatalogItem(Ingredient ingredient) {
        return new StockableCatalogItemData(
                ingredient.getId(),
                ingredient.getSku(),
                ingredient.getName(),
                CatalogItemType.INGREDIENT,
                ingredient.getStockUnit(),
                ingredient.isActive()
        );
    }

    private Category requireActiveCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        if (!category.isActive()) {
            throw new IllegalArgumentException("A categoria informada está inativa.");
        }
        return category;
    }

    private List<ProductIngredient> requireActiveIngredients(
            List<ProductIngredient> productIngredients
    ) {
        if (productIngredients == null || productIngredients.isEmpty()) {
            return List.of();
        }
        for (ProductIngredient productIngredient : productIngredients) {
            Ingredient ingredient = ingredientRepository.findById(
                    productIngredient.ingredientId()
            ).orElseThrow(() ->
                    new br.com.pasteldahora.catalog.domain.exception.IngredientNotFoundException(
                            productIngredient.ingredientId()
                    )
            );
            if (!ingredient.isActive()) {
                throw new IllegalArgumentException(
                        "O ingrediente " + ingredient.getName() + " está inativo."
                );
            }
        }
        return List.copyOf(productIngredients);
    }

}
