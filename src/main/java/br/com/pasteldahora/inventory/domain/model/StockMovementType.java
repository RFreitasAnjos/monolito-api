package br.com.pasteldahora.inventory.domain.model;

public enum StockMovementType {

    PURCHASE_ENTRY("Entrada de compra", true, true),
    MANUAL_ENTRY("Entrada manual", true, false),
    SALE_EXIT("Saída por venda", false, true),
    LOSS_EXIT("Saída por perda", false, false),
    MANUAL_EXIT("Saída manual", false, false);

    private final String description;
    private final boolean entry;
    private final boolean unitPriceRequired;

    StockMovementType(String description, boolean entry, boolean unitPriceRequired) {
        this.description = description;
        this.entry = entry;
        this.unitPriceRequired = unitPriceRequired;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEntry() {
        return entry;
    }

    public boolean isUnitPriceRequired() {
        return unitPriceRequired;
    }
}
