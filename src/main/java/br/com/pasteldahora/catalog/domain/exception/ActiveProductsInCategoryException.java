package br.com.pasteldahora.catalog.domain.exception;

public class ActiveProductsInCategoryException extends RuntimeException {

    public ActiveProductsInCategoryException() {
        super("Não é possível desativar uma categoria que possui produtos ativos.");
    }
}
