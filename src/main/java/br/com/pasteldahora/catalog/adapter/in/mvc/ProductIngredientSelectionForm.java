package br.com.pasteldahora.catalog.adapter.in.mvc;

import br.com.pasteldahora.catalog.domain.model.Ingredient;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductIngredientSelectionForm {

    private UUID ingredientId;
    private String ingredientName;
    private UnitOfMeasure stockUnit;
    private boolean selected;
    private BigDecimal quantity;

    static ProductIngredientSelectionForm from(
            Ingredient ingredient,
            ProductIngredient selection
    ) {
        ProductIngredientSelectionForm form = new ProductIngredientSelectionForm();
        form.setIngredientId(ingredient.getId());
        form.setIngredientName(ingredient.getName());
        form.setStockUnit(ingredient.getStockUnit());
        form.setSelected(selection != null);
        form.setQuantity(selection == null ? null : selection.quantity());
        return form;
    }

    public UUID getIngredientId() { return ingredientId; }
    public void setIngredientId(UUID ingredientId) { this.ingredientId = ingredientId; }
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
    public UnitOfMeasure getStockUnit() { return stockUnit; }
    public void setStockUnit(UnitOfMeasure stockUnit) { this.stockUnit = stockUnit; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
