package br.com.pasteldahora.inventory.domain.exception;

public class ProductNotEligibleForStockException extends RuntimeException {

    public ProductNotEligibleForStockException(String message) {
        super(message);
    }
}
