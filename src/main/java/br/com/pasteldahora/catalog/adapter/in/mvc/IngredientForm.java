package br.com.pasteldahora.catalog.adapter.in.mvc;

import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class IngredientForm {

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
    private UnitOfMeasure stockUnit;

    static IngredientForm from(Ingredient ingredient) {
        IngredientForm form = new IngredientForm();
        form.setSku(ingredient.getSku());
        form.setName(ingredient.getName());
        form.setDescription(ingredient.getDescription());
        form.setStockUnit(ingredient.getStockUnit());
        return form;
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public UnitOfMeasure getStockUnit() { return stockUnit; }
    public void setStockUnit(UnitOfMeasure stockUnit) { this.stockUnit = stockUnit; }
}
