package br.com.pasteldahora.inventory.domain.exception;

import java.util.UUID;

public class StockItemNotFoundException extends RuntimeException {

    public StockItemNotFoundException(UUID id) {
        super("Item de estoque não encontrado: " + id + ".");
    }
}
