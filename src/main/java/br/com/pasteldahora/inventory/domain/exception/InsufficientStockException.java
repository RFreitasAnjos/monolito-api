package br.com.pasteldahora.inventory.domain.exception;

import java.math.BigDecimal;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(BigDecimal available, BigDecimal requested) {
        super(
                "Estoque insuficiente. Disponível: "
                        + available.toPlainString()
                        + ", solicitado: "
                        + requested.toPlainString()
                        + "."
        );
    }
}
