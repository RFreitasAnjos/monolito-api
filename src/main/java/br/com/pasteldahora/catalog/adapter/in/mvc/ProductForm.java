package br.com.pasteldahora.catalog.adapter.in.mvc;

import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.Product;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductForm {

    @NotBlank
    @Size(max = 40)
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9._-]*$")
    private String sku;

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private UUID categoryId;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal salePrice;

    @NotNull
    private InventoryPolicy inventoryPolicy;

    private boolean customizable;

    private List<ProductIngredientSelectionForm> ingredients = new ArrayList<>();

    public static ProductForm create(List<Ingredient> ingredients) {
        ProductForm form = new ProductForm();
        form.ingredients = ingredients.stream()
                .map(ingredient -> ProductIngredientSelectionForm.from(ingredient, null))
                .toList();
        return form;
    }

    public static ProductForm from(Product product, List<Ingredient> ingredients) {
        ProductForm form = new ProductForm();
        form.setSku(product.getSku());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setCategoryId(product.getCategoryId());
        form.setSalePrice(product.getSalePrice());
        form.setInventoryPolicy(product.getInventoryPolicy());
        form.setCustomizable(product.isCustomizable());
        Map<UUID, ProductIngredient> selections = product.getIngredients().stream()
                .collect(Collectors.toMap(
                        ProductIngredient::ingredientId,
                        Function.identity()
                ));
        form.ingredients = ingredients.stream()
                .map(ingredient -> ProductIngredientSelectionForm.from(
                        ingredient,
                        selections.get(ingredient.getId())
                ))
                .toList();
        return form;
    }

    public List<ProductIngredient> selectedIngredients() {
        return ingredients.stream()
                .filter(ProductIngredientSelectionForm::isSelected)
                .map(selection -> new ProductIngredient(
                        selection.getIngredientId(),
                        selection.getQuantity()
                ))
                .toList();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public InventoryPolicy getInventoryPolicy() {
        return inventoryPolicy;
    }

    public void setInventoryPolicy(InventoryPolicy inventoryPolicy) {
        this.inventoryPolicy = inventoryPolicy;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    public void setCustomizable(boolean customizable) {
        this.customizable = customizable;
    }

    public List<ProductIngredientSelectionForm> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ProductIngredientSelectionForm> ingredients) {
        this.ingredients = ingredients == null ? new ArrayList<>() : ingredients;
    }
}
