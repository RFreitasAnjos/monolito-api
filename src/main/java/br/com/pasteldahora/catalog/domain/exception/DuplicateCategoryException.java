package br.com.pasteldahora.catalog.domain.exception;

public class DuplicateCategoryException extends RuntimeException {

    public DuplicateCategoryException(String name) {
        super("Já existe uma categoria cadastrada com o nome: " + name + ".");
    }
}
