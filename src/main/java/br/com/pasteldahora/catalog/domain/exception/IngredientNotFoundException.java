package br.com.pasteldahora.catalog.domain.exception;

import java.util.UUID;

public class IngredientNotFoundException extends RuntimeException {

    public IngredientNotFoundException(UUID id) {
        super("Insumo não encontrado: " + id + ".");
    }
}
