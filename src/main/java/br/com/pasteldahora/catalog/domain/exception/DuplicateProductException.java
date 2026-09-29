package br.com.pasteldahora.catalog.domain.exception;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String sku) {
        super("Já existe um produto cadastrado com o SKU: " + sku + ".");
    }
}
