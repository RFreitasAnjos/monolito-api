package br.com.pasteldahora.catalog.domain.model;

public enum CatalogItemType {
    PRODUCT("Produto"),
    INGREDIENT("Insumo");

    private final String description;

    CatalogItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
