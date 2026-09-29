package br.com.pasteldahora.catalog.domain.exception;

public class ActiveProductsUsingIngredientException extends RuntimeException {

    public ActiveProductsUsingIngredientException() {
        super("O ingrediente não pode ser desativado enquanto estiver associado a produtos ativos.");
    }
}
