package br.com.pasteldahora.catalog.adapter.in.api;

import br.com.pasteldahora.catalog.application.port.in.*;
import br.com.pasteldahora.catalog.domain.exception.CategoryNotFoundException;
import br.com.pasteldahora.catalog.domain.exception.ProductNotFoundException;
import br.com.pasteldahora.catalog.domain.model.Category;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final ProductUseCase productUseCase;
    private final CategoryUseCase categoryUseCase;
    private final IngredientUseCase ingredientUseCase;

    public CatalogController(
            ProductUseCase productUseCase,
            CategoryUseCase categoryUseCase,
            IngredientUseCase ingredientUseCase
    ) {
        this.productUseCase = productUseCase;
        this.categoryUseCase = categoryUseCase;
        this.ingredientUseCase = ingredientUseCase;
    }

    @GetMapping("/products")
    public CatalogProductPageResponse findProducts(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "NAME_ASC") ProductSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Map<UUID, Category> categories = activeCategoriesById();
        if (categoryId != null && !categories.containsKey(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        var products = productUseCase.search(new ProductSearchQuery(
                name,
                true,
                categoryId,
                minPrice,
                maxPrice,
                sort,
                page,
                size
        ));
        return CatalogProductPageResponse.from(
                products,
                categories,
                activeIngredientsById()
        );
    }

    @GetMapping("/products/{id}")
    public CatalogProductResponse findProductById(@PathVariable UUID id) {
        Product product = productUseCase.findById(id);
        if (!product.isActive()) {
            throw new ProductNotFoundException(id);
        }
        Category category = categoryUseCase.findById(product.getCategoryId());
        if (!category.isActive()) {
            throw new ProductNotFoundException(id);
        }
        return CatalogProductResponse.from(product, category, activeIngredientsById());
    }

    @GetMapping("/categories")
    public java.util.List<CatalogCategoryResponse> findCategories() {
        return categoryUseCase.findActive().stream()
                .map(CatalogCategoryResponse::from)
                .toList();
    }

    @GetMapping("/categories/{id}")
    public CatalogCategoryResponse findCategoryById(@PathVariable UUID id) {
        Category category = categoryUseCase.findById(id);
        if (!category.isActive()) {
            throw new CategoryNotFoundException(id);
        }
        return CatalogCategoryResponse.from(category);
    }

    @GetMapping("/ingredients")
    public java.util.List<CatalogIngredientResponse> findIngredients() {
        return ingredientUseCase.findActive().stream()
                .map(CatalogIngredientResponse::available)
                .toList();
    }

    private Map<UUID, Category> activeCategoriesById() {
        return categoryUseCase.findActive().stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
    }

    private Map<UUID, Ingredient> activeIngredientsById() {
        return ingredientUseCase.findActive().stream()
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));
    }
}
