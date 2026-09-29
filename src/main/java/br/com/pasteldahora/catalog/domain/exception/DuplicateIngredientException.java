package br.com.pasteldahora.catalog.domain.exception;

public class DuplicateIngredientException extends RuntimeException {

    public DuplicateIngredientException(String sku) {
        super("Já existe um insumo cadastrado com o SKU: " + sku + ".");
    }
}
