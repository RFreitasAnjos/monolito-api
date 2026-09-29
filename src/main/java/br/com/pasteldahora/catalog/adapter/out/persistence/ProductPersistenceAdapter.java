package br.com.pasteldahora.catalog.adapter.out.persistence;

import br.com.pasteldahora.catalog.application.port.in.PageResult;
import br.com.pasteldahora.catalog.application.port.in.ProductSearchQuery;
import br.com.pasteldahora.catalog.application.port.out.ProductRepositoryPort;
import br.com.pasteldahora.catalog.domain.model.Product;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository repository;

    ProductPersistenceAdapter(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        return toDomain(repository.save(toEntity(product)));
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return repository.findById(productId).map(ProductPersistenceAdapter::toDomain);
    }

    @Override
    public PageResult<Product> search(ProductSearchQuery query) {
        PageRequest pageRequest = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(
                        query.sort().isAscending()
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        query.sort().getProperty()
                )
        );
        Page<Product> page = repository.findAll(specification(query), pageRequest)
                .map(ProductPersistenceAdapter::toDomain);
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public boolean existsBySku(String sku) {
        return repository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, UUID id) {
        return repository.existsBySkuAndIdNot(sku, id);
    }

    @Override
    public boolean existsByCategoryIdAndActiveTrue(UUID categoryId) {
        return repository.existsByCategoryIdAndActiveTrue(categoryId);
    }

    @Override
    public boolean existsByIngredientIdAndActiveTrue(UUID ingredientId) {
        return repository.existsActiveProductUsingIngredient(ingredientId);
    }

    @Override
    public List<Product> findActiveByInventoryPolicy(InventoryPolicy inventoryPolicy) {
        return repository.findByActiveTrueAndInventoryPolicyOrderByNameAsc(inventoryPolicy)
                .stream()
                .map(ProductPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public List<Product> findActive() {
        return repository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(ProductPersistenceAdapter::toDomain)
                .toList();
    }

    private static Specification<ProductJpaEntity> specification(ProductSearchQuery query) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!query.term().isBlank()) {
                String term = "%" + query.term().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("name")), term),
                        builder.like(builder.lower(root.get("sku")), term)
                ));
            }
            if (query.active() != null) {
                predicates.add(builder.equal(root.get("active"), query.active()));
            }
            if (query.categoryId() != null) {
                predicates.add(builder.equal(root.get("categoryId"), query.categoryId()));
            }
            if (query.minimumPrice() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get("salePrice"),
                        query.minimumPrice()
                ));
            }
            if (query.maximumPrice() != null) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get("salePrice"),
                        query.maximumPrice()
                ));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static ProductJpaEntity toEntity(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getSalePrice(),
                product.getInventoryPolicy(),
                product.isCustomizable(),
                product.getIngredients().stream()
                        .map(item -> new ProductIngredientEmbeddable(
                                item.ingredientId(),
                                item.quantity()
                        ))
                        .toList(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getCreatedBy(),
                product.getUpdatedBy(),
                product.getDeactivatedAt(),
                product.getDeactivatedBy(),
                product.getVersion()
        );
    }

    private static Product toDomain(ProductJpaEntity entity) {
        return Product.restore(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategoryId(),
                entity.getSalePrice(),
                entity.getInventoryPolicy(),
                entity.isCustomizable(),
                entity.getIngredients().stream()
                        .map(item -> new ProductIngredient(
                                item.getIngredientId(),
                                item.getQuantity()
                        ))
                        .toList(),
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
