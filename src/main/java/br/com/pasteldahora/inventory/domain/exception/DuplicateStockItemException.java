package br.com.pasteldahora.inventory.domain.exception;

public class DuplicateStockItemException extends RuntimeException {

    public DuplicateStockItemException() {
        super("O produto já possui um item de estoque cadastrado.");
    }
}
