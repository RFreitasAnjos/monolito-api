package br.com.pasteldahora.catalog.domain.model;

/**
 * Define como a disponibilidade de um produto será controlada pelo módulo
 * Inventory.
 */
public enum InventoryPolicy {

    NOT_CONTROLLED("Sem controle de estoque"),
    DIRECT_STOCK("Estoque direto do produto"),
    RECIPE_BASED("Estoque calculado pelos insumos da receita");

    private final String description;

    InventoryPolicy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
