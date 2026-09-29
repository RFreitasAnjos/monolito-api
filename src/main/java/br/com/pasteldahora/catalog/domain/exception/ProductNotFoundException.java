package br.com.pasteldahora.catalog.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productName) {
        super("Já existe um produto cadastrado com o nome: " + productName + ".");
    }
}
