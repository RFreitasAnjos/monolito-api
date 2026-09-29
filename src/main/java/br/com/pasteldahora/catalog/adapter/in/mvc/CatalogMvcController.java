package br.com.pasteldahora.catalog.adapter.in.mvc;

import br.com.pasteldahora.catalog.application.port.in.CategoryUseCase;
import br.com.pasteldahora.catalog.application.port.in.CreateCategoryCommand;
import br.com.pasteldahora.catalog.application.port.in.CreateProductCommand;
import br.com.pasteldahora.catalog.application.port.in.CreateIngredientCommand;
import br.com.pasteldahora.catalog.application.port.in.IngredientUseCase;
import br.com.pasteldahora.catalog.application.port.in.ProductSearchQuery;
import br.com.pasteldahora.catalog.application.port.in.ProductUseCase;
import br.com.pasteldahora.catalog.application.port.in.UpdateCategoryCommand;
import br.com.pasteldahora.catalog.application.port.in.UpdateProductCommand;
import br.com.pasteldahora.catalog.application.port.in.UpdateIngredientCommand;
import br.com.pasteldahora.catalog.domain.exception.DuplicateCategoryException;
import br.com.pasteldahora.catalog.domain.exception.DuplicateProductException;
import br.com.pasteldahora.catalog.domain.exception.DuplicateIngredientException;
import br.com.pasteldahora.catalog.domain.exception.ActiveProductsInCategoryException;
import br.com.pasteldahora.catalog.domain.exception.ActiveProductsUsingIngredientException;
import br.com.pasteldahora.catalog.domain.model.Category;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.catalog.domain.model.Product;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee/catalog")
public class CatalogMvcController {

    private final ProductUseCase productUseCase;
    private final CategoryUseCase categoryUseCase;
    private final IngredientUseCase ingredientUseCase;

    public CatalogMvcController(
            ProductUseCase productUseCase,
            CategoryUseCase categoryUseCase,
            IngredientUseCase ingredientUseCase
    ) {
        this.productUseCase = productUseCase;
        this.categoryUseCase = categoryUseCase;
        this.ingredientUseCase = ingredientUseCase;
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String listProducts(
            @RequestParam(defaultValue = "") String term,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication,
            Model model
    ) {
        var categories = categoryUseCase.findAll();
        Map<UUID, Category> categoriesById = categories.stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        model.addAttribute(
                "products",
                productUseCase.search(new ProductSearchQuery(
                        term,
                        active,
                        categoryId,
                        null,
                        null,
                        br.com.pasteldahora.catalog.application.port.in.ProductSort.NAME_ASC,
                        page,
                        10
                ))
        );
        model.addAttribute("categories", categories);
        model.addAttribute("categoriesById", categoriesById);
        model.addAttribute("term", term);
        model.addAttribute("activeFilter", active);
        model.addAttribute("categoryFilter", categoryId);
        model.addAttribute("canManage", canManage(authentication));
        return "catalog/list-products";
    }

    @GetMapping("/products/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String newProduct(Model model) {
        model.addAttribute("form", ProductForm.create(ingredientUseCase.findActive()));
        addProductOptions(model);
        return "catalog/create-product";
    }

    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String createProduct(
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addProductOptions(model);
            return "catalog/create-product";
        }
        try {
            productUseCase.create(new CreateProductCommand(
                    form.getSku(),
                    form.getName(),
                    form.getDescription(),
                    form.getCategoryId(),
                    form.getSalePrice(),
                    form.getInventoryPolicy(),
                    form.isCustomizable(),
                    form.selectedIngredients(),
                    authentication.getName()
            ));
        } catch (DuplicateProductException exception) {
            bindingResult.rejectValue("sku", "product.sku.duplicate", exception.getMessage());
            addProductOptions(model);
            return "catalog/create-product";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("product.invalid", exception.getMessage());
            addProductOptions(model);
            return "catalog/create-product";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Produto cadastrado com sucesso.");
        return "redirect:/employee/catalog/products";
    }

    @GetMapping("/products/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String editProduct(@PathVariable UUID id, Model model) {
        Product product = productUseCase.findById(id);
        model.addAttribute("product", product);
        model.addAttribute(
                "form",
                ProductForm.from(product, ingredientUseCase.findActive())
        );
        addProductOptions(model);
        return "catalog/edit-product";
    }

    @PostMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateProduct(
            @PathVariable UUID id,
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", productUseCase.findById(id));
            addProductOptions(model);
            return "catalog/edit-product";
        }
        try {
            productUseCase.update(new UpdateProductCommand(
                    id,
                    form.getSku(),
                    form.getName(),
                    form.getDescription(),
                    form.getCategoryId(),
                    form.getSalePrice(),
                    form.getInventoryPolicy(),
                    form.isCustomizable(),
                    form.selectedIngredients(),
                    authentication.getName()
            ));
        } catch (DuplicateProductException exception) {
            bindingResult.rejectValue("sku", "product.sku.duplicate", exception.getMessage());
            model.addAttribute("product", productUseCase.findById(id));
            addProductOptions(model);
            return "catalog/edit-product";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("product.invalid", exception.getMessage());
            model.addAttribute("product", productUseCase.findById(id));
            addProductOptions(model);
            return "catalog/edit-product";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso.");
        return "redirect:/employee/catalog/products";
    }

    @PostMapping("/products/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deactivateProduct(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        productUseCase.deactivate(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Produto desativado.");
        return "redirect:/employee/catalog/products";
    }

    @PostMapping("/products/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String reactivateProduct(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            productUseCase.reactivate(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Produto reativado.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/catalog/products";
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String listCategories(Authentication authentication, Model model) {
        model.addAttribute("categories", categoryUseCase.findAll());
        model.addAttribute("canManage", canManage(authentication));
        return "catalog/list-categories";
    }

    @GetMapping("/categories/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String newCategory(Model model) {
        model.addAttribute("form", new CategoryForm());
        return "catalog/create-category";
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String createCategory(
            @Valid @ModelAttribute("form") CategoryForm form,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "catalog/create-category";
        }
        try {
            categoryUseCase.create(new CreateCategoryCommand(
                    form.getName(),
                    authentication.getName()
            ));
        } catch (DuplicateCategoryException exception) {
            bindingResult.rejectValue("name", "category.name.duplicate", exception.getMessage());
            return "catalog/create-category";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Categoria cadastrada com sucesso.");
        return "redirect:/employee/catalog/categories";
    }

    @GetMapping("/categories/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String editCategory(@PathVariable UUID id, Model model) {
        Category category = categoryUseCase.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("form", CategoryForm.from(category));
        return "catalog/edit-category";
    }

    @PostMapping("/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateCategory(
            @PathVariable UUID id,
            @Valid @ModelAttribute("form") CategoryForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryUseCase.findById(id));
            return "catalog/edit-category";
        }
        try {
            categoryUseCase.update(new UpdateCategoryCommand(
                    id,
                    form.getName(),
                    authentication.getName()
            ));
        } catch (DuplicateCategoryException exception) {
            bindingResult.rejectValue("name", "category.name.duplicate", exception.getMessage());
            model.addAttribute("category", categoryUseCase.findById(id));
            return "catalog/edit-category";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Categoria atualizada com sucesso.");
        return "redirect:/employee/catalog/categories";
    }

    @PostMapping("/categories/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deactivateCategory(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            categoryUseCase.deactivate(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Categoria desativada.");
        } catch (ActiveProductsInCategoryException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/catalog/categories";
    }

    @PostMapping("/categories/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String reactivateCategory(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        categoryUseCase.reactivate(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Categoria reativada.");
        return "redirect:/employee/catalog/categories";
    }

    @GetMapping("/ingredients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public String listIngredients(Authentication authentication, Model model) {
        model.addAttribute("ingredients", ingredientUseCase.findAll());
        model.addAttribute("canManage", canManage(authentication));
        return "catalog/list-ingredients";
    }

    @GetMapping("/ingredients/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String newIngredient(Model model) {
        model.addAttribute("form", new IngredientForm());
        model.addAttribute("units", UnitOfMeasure.values());
        return "catalog/create-ingredient";
    }

    @PostMapping("/ingredients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String createIngredient(
            @Valid @ModelAttribute("form") IngredientForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("units", UnitOfMeasure.values());
            return "catalog/create-ingredient";
        }
        try {
            ingredientUseCase.create(new CreateIngredientCommand(
                    form.getSku(),
                    form.getName(),
                    form.getDescription(),
                    form.getStockUnit(),
                    authentication.getName()
            ));
        } catch (DuplicateIngredientException exception) {
            bindingResult.rejectValue("sku", "ingredient.sku.duplicate", exception.getMessage());
            model.addAttribute("units", UnitOfMeasure.values());
            return "catalog/create-ingredient";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Ingrediente cadastrado.");
        return "redirect:/employee/catalog/ingredients";
    }

    @GetMapping("/ingredients/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String editIngredient(@PathVariable UUID id, Model model) {
        Ingredient ingredient = ingredientUseCase.findById(id);
        model.addAttribute("ingredient", ingredient);
        model.addAttribute("form", IngredientForm.from(ingredient));
        model.addAttribute("units", UnitOfMeasure.values());
        return "catalog/edit-ingredient";
    }

    @PostMapping("/ingredients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateIngredient(
            @PathVariable UUID id,
            @Valid @ModelAttribute("form") IngredientForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredient", ingredientUseCase.findById(id));
            model.addAttribute("units", UnitOfMeasure.values());
            return "catalog/edit-ingredient";
        }
        try {
            ingredientUseCase.update(new UpdateIngredientCommand(
                    id,
                    form.getSku(),
                    form.getName(),
                    form.getDescription(),
                    form.getStockUnit(),
                    authentication.getName()
            ));
        } catch (DuplicateIngredientException exception) {
            bindingResult.rejectValue("sku", "ingredient.sku.duplicate", exception.getMessage());
            model.addAttribute("ingredient", ingredientUseCase.findById(id));
            model.addAttribute("units", UnitOfMeasure.values());
            return "catalog/edit-ingredient";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Ingrediente atualizado.");
        return "redirect:/employee/catalog/ingredients";
    }

    @PostMapping("/ingredients/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deactivateIngredient(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ingredientUseCase.deactivate(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Ingrediente desativado.");
        } catch (ActiveProductsUsingIngredientException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/catalog/ingredients";
    }

    @PostMapping("/ingredients/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String reactivateIngredient(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        ingredientUseCase.reactivate(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Ingrediente reativado.");
        return "redirect:/employee/catalog/ingredients";
    }

    private void addProductOptions(Model model) {
        model.addAttribute("categories", categoryUseCase.findActive());
        model.addAttribute(
                "inventoryPolicies",
                List.of(InventoryPolicy.NOT_CONTROLLED, InventoryPolicy.DIRECT_STOCK)
        );
        model.addAttribute("ingredientsAvailable", ingredientUseCase.findActive());
    }

    private static boolean canManage(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority ->
                "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_MANAGER".equals(authority.getAuthority())
        );
    }
}
