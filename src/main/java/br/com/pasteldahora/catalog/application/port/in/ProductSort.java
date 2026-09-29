package br.com.pasteldahora.catalog.application.port.in;

public enum ProductSort {
    NAME_ASC("name", true),
    NAME_DESC("name", false),
    PRICE_ASC("salePrice", true),
    PRICE_DESC("salePrice", false);

    private final String property;
    private final boolean ascending;

    ProductSort(String property, boolean ascending) {
        this.property = property;
        this.ascending = ascending;
    }

    public String getProperty() {
        return property;
    }

    public boolean isAscending() {
        return ascending;
    }
}
